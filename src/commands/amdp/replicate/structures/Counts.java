package commands.amdp.replicate.structures;

/**
 * Counts structure for keeping track of values during EM.
 *
 * Created by Sidd Karamcheti on 3/7/16.
 */
public class Counts {
    /* Count of Target word t aligned with Source word s indexed nTS.get(t).get(s)*/
    public final DefaultDict<String, DefaultDict<String, Double>> nTS;
    /* Count of Target word t aligned with anything - indexed nTO.get(s) */
    public final DefaultDict<String, Double> nTO;

    public Counts() {
        this.nTS = new DefaultDict<>(o -> new DefaultDict<>(0.0));
        this.nTO = new DefaultDict<>(0.0);
    }

}
