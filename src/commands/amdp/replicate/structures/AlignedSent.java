package commands.amdp.replicate.structures;

import commands.amdp.replicate.language.LanguageExpression;

import java.util.List;

/**
 * Structure that defines an AlignedSent object, consisting of two language expressions and an alignment
 * between them.
 *
 * Created by Sidd Karamcheti on 3/7/16.
 */
public class AlignedSent{
    protected final LanguageExpression source;
    protected final LanguageExpression target;
    protected final Alignment align;
    protected final double weight;

    /**
     * Creates an Aligned Sentence with words, target words (mots).
     *
     * @param source Expression of the source language
     * @param target Expression of the target language
     */
    public AlignedSent(LanguageExpression source, LanguageExpression target) {
        this.source = source;
        this.target = target;
        this.align = new Alignment();
        this.weight = 1.0;
    }

    public AlignedSent(LanguageExpression source, LanguageExpression target, double weight) {
        this.source = source;
        this.target = target;
        this.align = new Alignment();
        this.weight = weight;
    }

    public List<String> getSourceWords() {
        return this.source.getWords();
    }

    public List<String> getTargetWords() {
        return this.target.getWords();
    }

    public double getWeight(){
        return this.weight;
    }

    public Alignment getAlign() {
        return this.align;
    }

}

