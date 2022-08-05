import java.util.ArrayList;
import java.util.logging.Logger;

import moa.evaluation.CMM;
import moa.evaluation.EntropyCollection;
import moa.evaluation.F1;
import moa.evaluation.General;
import moa.evaluation.MeasureCollection;
import moa.evaluation.SSQ;
import moa.evaluation.Separation;
import moa.evaluation.SilhouetteCoefficient;
import moa.evaluation.StatisticalCollection;

public class ClusteringMeasures {

	public static MeasureCollection[] getMeasures(boolean[] selection){
		ArrayList<Class> measure_classes = new ArrayList<Class>();
		measure_classes = getMeasureSelection(selection);

		MeasureCollection[] measures = new MeasureCollection[measure_classes.size()];
		
		for(int i=0; i<measure_classes.size(); i++){
			try {
				MeasureCollection m = (MeasureCollection)measure_classes.get(i).newInstance();
				for(int j=0; j<m.getNumMeasures(); j++)
					m.setEnabled(j, true);
				
				measures[i] = m;

			} catch (Exception ex) {
				Logger.getLogger("Couldn't create Instance for " + measure_classes.get(i).getName());
				ex.printStackTrace();
			}
		}

		// setar quais medidas eu não quero
		measures[0].setEnabled(0,false); // F1-P
		measures[0].setEnabled(1,false); // F1-R
		
		measures[1].setEnabled(1,false); // CMM Basic
		measures[1].setEnabled(2,false); // CMM Missed
		measures[1].setEnabled(3,false); // CMM Misplaced
		measures[1].setEnabled(4,false); // CMM Noise
		measures[1].setEnabled(5,false); // CA Seperability
		measures[1].setEnabled(6,false); // CA Noise
		measures[1].setEnabled(7,false); // CA Model
		
		measures[3].setEnabled(1,false); // BSS-GT
		measures[3].setEnabled(2,false); // BSS-Ratio
		
		measures[5].setEnabled(0,false); // van Dongen

		return measures;
	}

	private static ArrayList<Class> getMeasureSelection(boolean[] selection){
		ArrayList<Class> mclasses = new ArrayList<Class>();
		
		if(selection[0])
			mclasses.add(General.class);
		if(selection[1])
			mclasses.add(ModifiedF1.class);
		if(selection[2])
			mclasses.add(EntropyCollection.class);
		if(selection[3])
			mclasses.add(CMM.class);
		if(selection[4])
			mclasses.add(SSQ.class);
		if(selection[5])
			mclasses.add(Separation.class);
		if(selection[6])
			mclasses.add(SilhouetteCoefficient.class);
		if(selection[7])
			mclasses.add(StatisticalCollection.class);
		
		return mclasses;
	} 
}
