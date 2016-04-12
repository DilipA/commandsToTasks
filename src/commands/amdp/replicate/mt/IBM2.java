package commands.amdp.replicate.mt;

import commands.amdp.replicate.language.LanguageExpression;
import commands.amdp.replicate.language.MachineLanguage;
import commands.amdp.replicate.language.NaturalLanguage;
import commands.amdp.replicate.structures.*;
import commands.model3.weaklysupervisedinterface.WeaklySupervisedLanguageModel;
import commands.model3.weaklysupervisedinterface.WeaklySupervisedTrainingInstance;
import logicalexpressions.LogicalExpression;
import logicalexpressions.PFAtom;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Core class for the IBM Model 2 Translation system. Learns translation probabilities and
 * alignment probabilities from a corpus of weakly aligned sentences. In this model, an alignment
 * probability is introduced, a(i | j,l,m), which predicts a source word position, given its
 * aligned target word's position.
 *
 * EM Breakdown:
 *      E - Step: In training collect following counts weighted by training data:
 *                  a) Number of times a source word is translated into a target word
 *                  b) Number of times a particular position in source is aligned to a particular
 *                     position in target.
 *
 *      M - Step: Estimate new probabilities from E - Step counts.
 *
 * Variables:
 *      i - Position in the source sentence { 0 (NULL), 1, 2, ... Length }
 *      j - Position in the target sentence { 1, 2, ... Length }
 *      l - Number of words in the source sentence (excluding NULL)
 *      m - Number of words in the target sentence
 *      s - Word in the source language
 *      t - Word in the target language
 *
 * References:
 *      NLTK Translate Library
 *      Philipp Koehn. 2010. Statistical Machine Translation.
 *
 * Created by Sidd Karamcheti on 3/8/16.
 */
public class IBM2 extends IBMModel implements MachineTranslator, WeaklySupervisedLanguageModel{
    /**
     * Instantiate an IBM Model 2 instance with a given Parallel Corpus, and a set number
     * of EM iterations.
     *
     * @param corpus Weakly aligned parallel corpus.
    */
    public IBM2(ParallelCorpus corpus) {
        super(corpus);
    }

    public void init(int em_iterations){
        // Initialize tau translation probabilities by running a few iterations of Model 1 training
        IBM1 ibm1 = new IBM1(corpus, 2 * em_iterations);
        this.tau = ibm1.tau;

        // Initialize all delta probabilities
        this.setUniformProbabilities();

        // Run EM
        for (int i = 0; i < em_iterations; i++) {
            this.train();
        }
    }

    /**
     * Set all alignment (Delta) probabilities to be uniform.
     */
    public void setUniformProbabilities() {
        // a(i | j, l, m) = 1 / (l + 1) for all i, j, l, m
//        HashSet<Pair<Integer, Integer>> lmCombinations = new HashSet<>();
//
//        for (int index = 0; index < this.corpus.size(); index++) {
//            AlignedSent alignedSent = this.corpus.get(index);
//            int l = alignedSent.getTargetWords().size();
//            int m = alignedSent.getSourceWords().size();
//
//            Pair<Integer, Integer> lm = new Pair<>(l, m);
//            if (!lmCombinations.contains(lm)) {
//                lmCombinations.add(lm);
//                double initialProb = 1.0 / (l + 1.0);
//
//                for (int i = 0; i < l + 1; i++) {
//                    for (int j = 1; j < m + 1; j++) {
//                        DefaultDict<Integer, Double> mProb = new DefaultDict<>(initialProb);
//                        DefaultDict<Integer, DefaultDict<Integer, Double>> lmProb = new DefaultDict<>(o -> mProb);
//                        DefaultDict<Integer, DefaultDict<Integer, DefaultDict<Integer, Double>>> jlmProb = new DefaultDict<>(o -> lmProb);
//                        this.delta.put(i, jlmProb);
//                    }
//                }
//            }
//        }

        double uniLen = 1.0 / this.corpus.getMaxSourceLength();
        for(int l=1;l <= this.corpus.getMaxTargetLength();l++){
            for(int m=1;m <= this.corpus.getMaxSourceLength();m++){
                this.lengthPrior.get(l).put(m, uniLen);

                for(int i=1;i <= m;i++){
                    double uniDist = 1.0/(l+1);
                    for(int j=0;j <= l;j++){
                        DefaultDict<Integer, Double> mProb = new DefaultDict<>(uniDist);
                        DefaultDict<Integer, DefaultDict<Integer, Double>> lmProb = new DefaultDict<>(o -> mProb);
                        DefaultDict<Integer, DefaultDict<Integer, DefaultDict<Integer, Double>>> jlmProb = new DefaultDict<>(o -> lmProb);
                        this.delta.put(j, jlmProb);
                    }
                }
            }
        }
    }

    /**
     * Run one iteration of EM, using the given tau and delta values as prior probabilities.
     */
    public void train() {
        AlignmentCounts counts = new AlignmentCounts();

        // E - Step
        for (int index = 0; index < this.corpus.size(); index++) {
            AlignedSent alignedSent = this.corpus.get(index);
            List<String> sourceSent = alignedSent.getTargetWords();
            List<String> targetSent = alignedSent.getSourceWords();

            // Prepend NULL Token
            List<String> nulled = new ArrayList<>();
            nulled.add(NULL);
            nulled.addAll(sourceSent);
            sourceSent = nulled;

            // Prepend Unused Token to Target (1 - Indexed)
            List<String> modTarget = new ArrayList<>();
            modTarget.add("UNUSED");
            modTarget.addAll(targetSent);
            targetSent = modTarget;

            int l = sourceSent.size() - 1;
            int m = targetSent.size() - 1;

            // E - Step (a) - Compute normalization factors
            DefaultDict<String, Double> totalCount = new DefaultDict<>(0.0);
            for (int j = 1; j < targetSent.size(); j++) {
                String t = targetSent.get(j);
                for (int i = 0; i < sourceSent.size(); i++) {
                    String s = sourceSent.get(i);
                    totalCount.put(t, totalCount.get(t) + (this.tau.get(t).get(s) * this.delta
                            .get(i).get(j).get(l).get(m)));
                }
            }

            // E - Step (b) - Compute counts
            for (int j = 1; j < targetSent.size(); j++) {
                String t = targetSent.get(j);
                for (int i = 0; i < sourceSent.size(); i++) {
                    String s = sourceSent.get(i);
                    double count = this.tau.get(t).get(s) * this.delta.get(i).get(j).get(l).get(m);
                    double normalizedCount = count / totalCount.get(t);
                    normalizedCount *= alignedSent.getWeight();

                    // Update tau counts
                    counts.nTS.get(t).put(s, counts.nTS.get(t).get(s) + normalizedCount);
                    counts.nTO.put(s, counts.nTO.get(s) + normalizedCount);
                    // Update delta counts
                    counts.nIJLM.get(i).get(j).get(l).put(m, counts.nIJLM.get(i).get(j).get(l).get
                            (m) + normalizedCount);
                    counts.nIO.get(j).get(l).put(m, counts.nIO.get(j).get(l).get(m) +
                            normalizedCount);
                }
            }
        }

        // M - Step
        // Reset Tau - Values
        for (String t : counts.nTS.keySet()) {
            for (String s : counts.nTS.get(t).keySet()) {
                double estimate = counts.nTS.get(t).get(s) / counts.nTO.get(s);
                this.tau.get(t).put(s, estimate);
            }
        }

        // Reset Delta - Values
        for (int i : counts.nIJLM.keySet()) {
            for (int j : counts.nIJLM.get(i).keySet()) {
                for (int l : counts.nIJLM.get(i).get(j).keySet()) {
                    for (int m : counts.nIJLM.get(i).get(j).get(l).keySet()) {
                        double estimate = counts.nIJLM.get(i).get(j).get(l).get(m) /
                                counts.nIO.get(j).get(l).get(m);
                        this.delta.get(i).get(j).get(l).put(m, estimate);
                    }
                }
            }
        }
    }

    /**
     * Translate a single expression of this IBM model's source language into an expression of the target language
     * @param sourceExpression A language expression in the model's source language
     * @return A language expression in the model's target language
     */
    @Override
    public LanguageExpression translate(LanguageExpression sourceExpression) {
        List<String> sourceSplit = sourceExpression.getWords();
        Map<String, Double> exprProbs = new HashMap<>();
        int m = sourceExpression.getWords().size();
        double maxLikelihood = Double.NEGATIVE_INFINITY;
        String likelyExpr = "";

        for (String expr : this.outputSet) {
            String[] exprSplit = expr.split(" ");
            int l = exprSplit.length;
            double likelihood = this.lengthPrior.get(l).get(m);
            double sum = 0.0;

            for (int a = 0; a < l; a++) {
                double product = 1.0;
                for (int k = 0; k < m; k++) {
                    product *= this.delta.get(a).get(k).get(l).get(m);
                    product *= this.tau.get(sourceSplit.get(k)).get(exprSplit[a]);
                }
                sum += product;
            }
            likelihood *= sum;
            if (likelihood > maxLikelihood) {
                maxLikelihood = likelihood;
                likelyExpr = expr;
            }
            exprProbs.put(expr, likelihood);
        }
        List<String> translated = Arrays.asList(likelyExpr.split(" "));
        exprProbs.entrySet().stream().sorted((e1,e2) -> e1.getValue().compareTo(e2.getValue())).forEachOrdered(System.out::println);

        return new MachineLanguage(translated);
    }

    public static boolean goodTranslation(List<String> actual, List<String> translated){
        AtomicBoolean ret = new AtomicBoolean(true);
        actual.stream().forEach(w -> ret.compareAndSet(!translated.contains(w), false));
        return ret.get();
    }

    public static double runLOOTest(ParallelCorpus corpus){
        AtomicInteger numCorrect = new AtomicInteger(0);
        IntStream.range(0, corpus.size()).forEachOrdered(i -> {
            AlignedSent test = corpus.remove(i);
            List<String> inputWords = test.getSourceWords();
            List<String> outputWords = test.getTargetWords();
            IBM2 ibm2 = new IBM2(corpus);
            ibm2.init(10);
            NaturalLanguage input = new NaturalLanguage(inputWords);
            List<String> output = ibm2.translate(input).getWords();
            if(goodTranslation(outputWords, output)){
                numCorrect.getAndIncrement();
                System.out.println("Performed correct translation");
                System.out.println("Correctly translated: " + output.toString());
            }
            else{
                System.out.println("Performed incorrect translation");
                System.out.println("Input: " + inputWords.toString());
                System.out.println("Expected: " + outputWords.toString());
                System.out.println("Found: " + output.toString());
            }
            System.out.println("");
            corpus.insert(test, i);
        });
        return (double) numCorrect.get() / (double) corpus.size();
    }

    public static void main(String[] args){
        String english = "data/corpus/expert_english.txt";
        String machine = "data/corpus/expert_machine.txt";
        ParallelCorpus corpus = new ParallelCorpus(english, machine);

        double accuracy = runLOOTest(corpus);
        System.out.println("LOO accuracy: " + accuracy);
    }

    /**
     * Takes task and binding constrained logical expressions and turns it into a machine language expression.
     * This method assumes that the children of the logical expressions are terminal {@link logicalexpressions.PFAtom}
     * objects.
     * @param liftedTask the lifted task
     * @param bindingConstraints the object binding constraints
     * @return the machine language expression.
     */
    protected String getMachineLanguageString(LogicalExpression liftedTask, LogicalExpression bindingConstraints){

        //assume flat list of PF Atoms for now
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < liftedTask.getChildExpressions().size(); i++){
            if(i > 0){
                sb.append(" ");
            }
            PFAtom atom = (PFAtom)liftedTask.getChildExpressions().get(i);
            sb.append(atom.getGroundedProp().pf.getName());
            String [] pfParams = atom.getGroundedProp().pf.getParameterClasses();
            for(String p : pfParams){
                sb.append(" ").append(p);
            }
        }

        for(int i = 0; i < bindingConstraints.getChildExpressions().size(); i++){
            sb.append(" ");
            PFAtom atom = (PFAtom)bindingConstraints.getChildExpressions().get(i);
            sb.append(atom.getGroundedProp().pf.getName());
            String [] pfParams = atom.getGroundedProp().pf.getParameterClasses();
            for(String p : pfParams){
                sb.append(" ").append(p);
            }
        }



        return sb.toString();

    }

    protected double sampleMargAlign(List<String> semanticCommand, List<String> naturalCommand, int nSamples){


        int l = semanticCommand.size();
        int m = naturalCommand.size();

//        List<String> nulledSemantic = new ArrayList<>();
//        nulledSemantic.add(NULL);
//        nulledSemantic.addAll(semanticCommand);
//
//        semanticCommand = nulledSemantic;
//
//        List<String> nulledNatural = new ArrayList<>();
//        nulledNatural.add(NULL);
//        nulledNatural.addAll(naturalCommand);
//        naturalCommand = nulledNatural;

        double alignMarg = 0.;
        for(int i = 0; i < nSamples; i++){
            List <Integer> alignment = this.sampleAlignment(l, m);
            double prod = 1.;
            for(int k = 1; k <= alignment.size(); k++){
                int ak = alignment.get(k-1); //note that alignment array is in 0-base index
//                System.out.println(alignment);
//                System.out.println(naturalCommand);
//                System.out.println(semanticCommand);
//                System.out.println(k + "\t" + ak);
                String pWord = k == 0 ? NULL : naturalCommand.get(k-1);
                String gWord = ak == 0 ? NULL : semanticCommand.get(ak-1);

//                if(!this.naturalWords.contains(pWord)){
//                    continue;
//                }

                double word = this.tau.get(pWord).get(gWord);

                prod *= word;
            }
            alignMarg += prod;

        }


        alignMarg /= (double)nSamples;

        return alignMarg;

    }

    protected double m1MaximumAlignment(List<String> semanticCommand, List<String> naturalCommand){


        int l = semanticCommand.size();
        int m = naturalCommand.size();

        double prod = 1.;
        for(int k = 1; k <= m; k++){

            String pWord = naturalCommand.get(k-1);
//            if(!this.naturalWords.contains(pWord)){
//                continue;
//            }

            //find best match
            double bestMatch = 0.;
            for(int j = 0; j <= l; j++){
                double word = this.tau.get(pWord).get(semanticCommand.get(j-1));
                if(word > bestMatch){
                    bestMatch = word;
                }
            }

            prod *= bestMatch;

        }

        //normalize by how many possible alignments there are
        double prob = prod / Math.pow(l+1, m);

        return prob;

    }

    protected List <Integer> sampleAlignment(int l, int m){
        List <Integer> alignment = new ArrayList<>(m);

        for(int i = 1; i <= m; i++){
            double r = new Random(1).nextDouble();
            double sumP = 0.;
            for(int j = 0; j <= l; j++){
                double p = this.delta.get(j).get(i).get(l).get(m);
                sumP += p;
                if(r < sumP){
                    alignment.add(j);
                    break;
                }
            }

        }


        return alignment;
    }
    
    public double computeNaturalCommandProb(String machine, String natural){
        List<String> machineWords = Arrays.asList(machine.split(" "));
        List<String> naturalWords = Arrays.asList(natural.split(" "));
        
        int l = machineWords.size();
        int m = naturalWords.size();
        
        double eta = this.lengthPrior.get(l).get(m);
        if(eta == 0.){
            //then is there any l for which this is not true?
            AtomicBoolean allLengthParamsZero = new AtomicBoolean(true);
            this.lengthPrior.keySet().stream().forEach(i -> {
                if(this.lengthPrior.get(i).get(m) > 0.){
                    allLengthParamsZero.set(false);
                    return;
                }
            });
            
            if(allLengthParamsZero.get()){
                eta = 1.;
            }
        }

        double alignMarg;
        if(eta > 0){
            alignMarg = this.sampleMargAlign(machineWords, naturalWords, 1000);
        }
        else{
            alignMarg = this.m1MaximumAlignment(machineWords, naturalWords);
        }

        double p = alignMarg * eta;

        return p;
    }

    @Override
    public double probabilityOfCommand(LogicalExpression liftedTask, LogicalExpression bindingConstraints, String command) {
        List<String> sourceSplit = Arrays.asList(command.split(" "));
        int m = sourceSplit.size();
        String expr = getMachineLanguageString(liftedTask, bindingConstraints);
        return this.computeNaturalCommandProb(expr, command);
        /*
        String[] exprSplit = expr.split(" ");
        int l = exprSplit.length;
        double likelihood = this.lengthPrior.get(l).get(m);
        double sum = 0.0;
        for (int a = 0; a < l; a++) {
            double product = 1.0;
            for (int k = 0; k < m; k++) {
                product *= this.delta.get(a).get(k).get(l).get(m);
                product *= this.tau.get(sourceSplit.get(k)).get(exprSplit[a]);
            }
            sum += product;
        }

        likelihood *= sum;
        return likelihood;
        */
    }

    public AlignedSent convertWSI(WeaklySupervisedTrainingInstance wsi){
        String naturalLangauge = wsi.command;
        String machineLanguage = this.getMachineLanguageString(wsi.liftedTask, wsi.bindingConstraints);
        double prob = wsi.weight;
        LanguageExpression sourceExpr = new NaturalLanguage(Arrays.asList(naturalLangauge.split(" ")));
        LanguageExpression targetExpr = new MachineLanguage(Arrays.asList(machineLanguage.split(" ")));
        AlignedSent alignedSent = new AlignedSent(sourceExpr, targetExpr, prob);
        return alignedSent;
    }

    @Override
    public void learnFromDataset(List<WeaklySupervisedTrainingInstance> dataset) {
        List<AlignedSent> alignedSents = dataset.stream().map(this::convertWSI).collect(Collectors.toList());
        ParallelCorpus datasetCorpus = new ParallelCorpus(alignedSents);
        this.corpus = datasetCorpus;
        this.remakePriors();
        this.init(15);
//        System.out.println("--------------");
//        this.tau.entrySet().stream().forEach(e -> System.out.println(e.getKey() + "\t" + e.getValue()));
//        System.out.println("**************");
//        this.delta.entrySet().stream().forEach(e -> System.out.println(e.getKey() + "\t" + e.getValue()));
//        System.out.println("%%%%%%%%%%%%%%");
    }
}
