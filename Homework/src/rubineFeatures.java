import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Vector;

import javax.xml.crypto.Data;

public class rubineFeatures {

    static class Datapoints {

        int x_val;
        int y_val;
        long timeval;

        public Datapoints(){

        }

        public Datapoints(int x, int y ,long z){

            this.x_val = x;
            this.y_val = y;
            this.timeval = z;
        }
    }

    static class Shape {
        String name;
        int sample_points;
        Vector<Datapoints> datapoints_vector = new Vector<Datapoints>();
    }

    static class Features{
        String name_shape;
        double []features;

        public Features() {

        }
        public Features(String name, double []vals) {
            this.name_shape = name;
            for(int i=0;i<vals.length;i++){
                this.features[i] = vals[i];
            }
        }

    }

    public static double[] calc_feature_cos_sin(double x0, double x1, double y0, double y1){
        double num_cos = (x1 - x0);
        double num_sin = (y1 - y0);
        double denom = Math.sqrt( Math.pow((x1-x0), 2) + Math.pow((y1-y0), 2));

        double[]cossin = new double[2];
        cossin[0] = num_cos/denom;
        cossin[1] = num_sin/denom;
        return cossin;
    }

    public static double cal_feature_diagonal(double x0, double xn, double y0 , double yn){
        double feature ;
        feature = Math.sqrt( Math.pow((xn-x0), 2) + Math.pow((yn-y0), 2) );
        return feature;
    }

    public static double[] cal_min_max(Vector<Datapoints> datavector){

        double[] feature_rest = new double[2];

        double xmin = datavector.elementAt(0).x_val;
        double xmax = datavector.elementAt(0).x_val;
        double ymin = datavector.elementAt(0).y_val;
        double ymax = datavector.elementAt(0).y_val;
        int size_each_datavector = datavector.size();

        for(int j=1; j< size_each_datavector; j++){
            //Cal calculatin min/max
            if(xmin > datavector.elementAt(j).x_val)
                xmin = datavector.elementAt(j).x_val;
            if(ymin > datavector.elementAt(j).y_val)
                ymin = datavector.elementAt(j).y_val;
            if(xmax < datavector.elementAt(j).x_val)
                xmax = datavector.elementAt(j).x_val;
            if(ymax < datavector.elementAt(j).y_val)
                ymax =datavector.elementAt(j).y_val;
        }
        feature_rest[0] = Math.sqrt(Math.pow((ymax-ymin), 2) + Math.pow((xmax-xmin), 2));
        feature_rest[1] = Math.atan2((ymax-ymin), (xmax-xmin));
        return feature_rest;

    }

    public static double[] cal_feature8_11(Vector<Datapoints> datavector){
        double deltaxp1 = 0,deltaxp2 = 0,deltayp1 = 0,deltayp2= 0;
        double [] features_8_9_10_11 = new double[4];
        Vector<Datapoints> Cleaned_data = new Vector<Datapoints>();
        int size_each_datavector = datavector.size();

        //Cleaning the data to remove all the similiar points
        Datapoints prev_vector = datavector.elementAt(0);
        Cleaned_data.add(datavector.elementAt(0));
        for (int i =1 ; i < size_each_datavector; i++) {
            if(datavector.elementAt(i).x_val == prev_vector.x_val && datavector.elementAt(i).y_val == prev_vector.y_val && datavector.elementAt(i).timeval != prev_vector.timeval)	{
                continue;
            }
            else{
                prev_vector = datavector.elementAt(i);
                Cleaned_data.add(datavector.elementAt(i));
            }

        }

        for(int j=0; j< Cleaned_data.size()-1; j++){

            deltayp2 = (Cleaned_data.elementAt(j+1).y_val
                    - Cleaned_data.elementAt(j).y_val);
            deltaxp2 = (Cleaned_data.elementAt(j+1).x_val
                    - Cleaned_data.elementAt(j).x_val);

            if(j >0){

                deltaxp1 = (Cleaned_data.elementAt(j).x_val
                        - Cleaned_data.elementAt(j-1).x_val);
                deltayp1 = (Cleaned_data.elementAt(j).y_val
                        - Cleaned_data.elementAt(j-1).y_val);

                double inter_val = Math.atan2(((deltaxp2 * deltayp1) - 	(deltaxp1 * deltayp2) ),((deltaxp1*deltaxp2) + (deltayp1 * deltayp2)));

                //System.out.println(inter_val);
                features_8_9_10_11[1] += inter_val;
                features_8_9_10_11[2] += Math.abs(inter_val);
                features_8_9_10_11[3] += Math.pow(inter_val, 2);

            }

            features_8_9_10_11[0] += Math.sqrt(Math.pow( (deltaxp2),2) +  Math.pow((deltayp2), 2));

        }
        return features_8_9_10_11;
    }

    public static double[] calc_time_featuers(Vector<Datapoints> datavector)
    {

        long deltatp, deltayp2, deltaxp2;
        double [] features_11_12 = new double[2];
        features_11_12[0] =Double.MIN_VALUE;
        Vector<Datapoints> Cleaned_data = new Vector<Datapoints>();
        int size_each_datavector = datavector.size();

        //Cleaning the data to remove all the similiar points
        Datapoints prev_vector = datavector.elementAt(0);
        Cleaned_data.add(datavector.elementAt(0));
        for (int i = 1; i < size_each_datavector; i++) {
            if( datavector.elementAt(i).timeval == prev_vector.timeval ){
                continue;
            }
            else{
                prev_vector = datavector.elementAt(i);
                Cleaned_data.add(datavector.elementAt(i));
            }

        }

        for(int j=0; j< Cleaned_data.size()-1; j++){

            deltatp = (Cleaned_data.elementAt(j+1).timeval - Cleaned_data.elementAt(j).timeval);

            deltayp2 = (Cleaned_data.elementAt(j+1).y_val
                    - Cleaned_data.elementAt(j).y_val);
            deltaxp2 = (Cleaned_data.elementAt(j+1).x_val
                    - Cleaned_data.elementAt(j).x_val);


            double speed= Double.MIN_VALUE;
            if(deltatp!=0)
                speed= (Math.pow(deltaxp2,2) + Math.pow(deltayp2,2)) / Math.pow(deltatp, 2);

            if(speed > features_11_12[0])
                features_11_12[0] = speed;
        }

        features_11_12[1] = Cleaned_data.lastElement().timeval - Cleaned_data.elementAt(0).timeval;
        return features_11_12;

    }
    public static void main(String[] args) throws Exception {

        String basename = args[0];
        //String basename = "C:\\Users\\AADY\\Desktop\\data\\shape";
        int number_of_shapes = 20;
        //int number_of_shapes = 8;
        boolean first_chance = false;

        for(int alpha ='a' ; alpha< 'a'+26;alpha++){
            char val = (char) alpha;
            String base_letter = Character.toString(val);

            Vector<Shape> shapes = new Vector<Shape>();
            Vector<Features> computed_features = new Vector<Features>();

            for(int i=1; i<= number_of_shapes ; i++){

                Shape newshape = new Shape();
                newshape.name = "base_letter";

                String filename = basename+"\\"+ base_letter+ "\\"+ base_letter +"_"+ + i + ".txt";
                //String filename = basename+ + i + ".txt";
                File file = new File(filename);

                BufferedReader br = new BufferedReader(new FileReader(file));

                String line = null;
                while ((line = br.readLine()) != null) {
                    String[] inputArr = line.split(",|;");
                    Datapoints temp = new Datapoints();
                    temp.x_val = Integer.parseInt(inputArr[0].trim());
                    temp.y_val = Integer.parseInt(inputArr[1].trim());
                    temp.timeval = Long.parseLong(inputArr[2].trim());
                    newshape.datapoints_vector.addElement(temp);
                }

                br.close();

                shapes.addElement(newshape);

            }

            //After getting the data we are now calculating the values of all the features corresponding the shape given.

            for(int i=0 ; i< number_of_shapes ; i++){

                Features newfeature = new Features();
                newfeature.name_shape = base_letter;

                // calculating the features one and two
                double []cossin_start = calc_feature_cos_sin(shapes.elementAt(i).datapoints_vector.elementAt(0).x_val,
                        shapes.elementAt(i).datapoints_vector.elementAt(2).x_val,
                        shapes.elementAt(i).datapoints_vector.elementAt(0).y_val,
                        shapes.elementAt(i).datapoints_vector.elementAt(2).y_val);

                //calculating the features six and seven
                double []cossin_end = calc_feature_cos_sin(shapes.elementAt(i).datapoints_vector.elementAt(0).x_val,
                        shapes.elementAt(i).datapoints_vector.lastElement().x_val,
                        shapes.elementAt(i).datapoints_vector.elementAt(0).y_val,
                        shapes.elementAt(i).datapoints_vector.lastElement().y_val);

                double feature5 = cal_feature_diagonal(shapes.elementAt(i).datapoints_vector.elementAt(0).x_val,
                        shapes.elementAt(i).datapoints_vector.lastElement().x_val,
                        shapes.elementAt(i).datapoints_vector.elementAt(0).y_val,
                        shapes.elementAt(i).datapoints_vector.lastElement().y_val);

                double[] features_3_4= cal_min_max(shapes.elementAt(i).datapoints_vector);
                double[] cal_feature8_11 = cal_feature8_11(shapes.elementAt(i).datapoints_vector);

                double[] featureset = new double[13];

                featureset[0] = cossin_start[0];
                featureset[1] = cossin_start[1];

                featureset[4] = feature5;
                featureset[5] = cossin_end[0];
                featureset[6] = cossin_end[1];

                // feature 2 & 3 are calculated as it needs to be run in loop so for optimization i calculated with other features which also need loop

                featureset[2] = features_3_4[0];
                featureset[3] = features_3_4[1];

                featureset[7] =  cal_feature8_11[0];
                featureset[8] =  cal_feature8_11[1];
                featureset[9] =  cal_feature8_11[2];
                featureset[10] = cal_feature8_11[3];

                double[] time_featuers = calc_time_featuers(shapes.elementAt(i).datapoints_vector);
                featureset[11] = time_featuers[0];
                featureset[12] = time_featuers[1];

                newfeature.features = featureset;

                //Adding the features in the list
                computed_features.addElement(newfeature);

            }

            // Creating the output csv file.
            String csvFile = "./Rubinefeatures.csv";
            BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile,true));

            if(!first_chance){
                writer.write("Label, F1, F2 ,F3,F4, F5 ,F6,F7, F8 ,F9,F10, F11 ,F12,F13");
                first_chance = true;
            }

            for(int i=0; i< number_of_shapes; i++){
                writer.write(System.getProperty( "line.separator" ));
                writer.write(computed_features.elementAt(i).name_shape+",");
                for(int j=0; j< 13; j++)
                    writer.write(String.valueOf(computed_features.elementAt(i).features[j])+ ",");
            }
            writer.close();
            //System.out.println("Total number of shapes we have loaded is " + shapes.size());
        }


    }

}
