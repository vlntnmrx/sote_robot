package hsurobots.components;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AutoSteuerung extends Steuerung {
    private List<SensorData> lastData = new LinkedList<>();
    private int leerlauf = 0;

    @Override
    public void move(double deltaTimeSec) {
        if (lastData.size() > 3) {
            velocity = 300;
            double[] sur = new double[100];
            Arrays.fill(sur, 100);
            for (int i = 0; i < lastData.size(); i++) {
                int pos = (int) (((double) sur.length) * (lastData.get(i).getAngle() + lastData.get(i).getRelatedSensor().getOrientationToRobot()) / (2 * Math.PI));
                while (pos <= -sur.length)
                    pos += sur.length;
                while (pos >= sur.length)
                    pos -= sur.length;
                if (pos < 0)
                    pos = sur.length + pos;

                sur[pos] = lastData.get(i).getDistance();
            }
            sur = smooth(sur, 20);

            double min_view = 100;
            int hindernis = -1;
            int view = 9;
            int i;
            float avg_r = 100;
            float avg_l = 100;
            for (i = 0; i < sur.length / view; i++) {
                if (min_view > sur[i]) {
                    min_view = sur[i];
                    hindernis = i;
                }
                avg_r += sur[i];
            }
            avg_r = avg_r / (float) (sur.length / view);
            for (i = sur.length - (sur.length / view); i < sur.length; i++) {
                if (min_view > sur[i]) {
                    min_view = sur[i];
                    hindernis = i;
                }
                avg_l += sur[i];
            }
            avg_l = avg_l / (float) (sur.length - (sur.length / view));
            if (min_view < 30) {
                velocity = 0;
                if (avg_l > avg_r) {
                    rotate(-0.1);
                } else {
                    rotate(0.1);
                }
            } else {
                if (hindernis >= 0 && hindernis < sur.length / view) {
                    rotate(-0.1);
                } else if (hindernis >= 0 && hindernis > sur.length - (sur.length / view)) {
                    rotate(0.1);
                }
            }
            lastData.clear();
            leerlauf = 0;
        } else if (lastData.size() < 3) {
            if (orientation < 0 || (orientation > Math.PI)) {
                rotate(0.05);
            } else {
                rotate(-0.05);
            }
            leerlauf++;
        } else {
            leerlauf++;
        }
        if (leerlauf > 5) {
            lastData.clear();
            leerlauf = 0;
        }
        super.move(deltaTimeSec);
    }

    public void updateData(SensorData data) {
        //Gibts schon daten von dem Sensor?
        for (int i = 0; i < lastData.size(); i++) {
            if (lastData.get(i).getRelatedSensor().equals(data.getRelatedSensor())) {
                lastData.set(i, data);
                return;
            }
        }
        //Wenn nicht, dann hinten anfügen
        lastData.add(data);
    }

    public double[] smooth(double[] arr, int strength) {
        for (int i = 0; i < strength; i++) {
            for (int j = 0; j < arr.length; j++) {
                double l_3 = (j - 3 < 0 ? arr[arr.length - 3 + j] : arr[j - 3]);
                double l_2 = (j - 2 < 0 ? arr[arr.length - 2 + j] : arr[j - 2]);
                double l_1 = (j - 1 < 0 ? arr[arr.length - 1 + j] : arr[j - 1]);

                double n_1 = (j + 1 >= arr.length ? arr[arr.length - j + 1] : arr[j + 1]);
                double n_2 = (j + 2 >= arr.length ? arr[arr.length - j + 2] : arr[j + 2]);
                double n_3 = (j + 3 >= arr.length ? arr[arr.length - j + 3] : arr[j + 3]);
                if (arr[j] < l_1 && arr[j] < n_1)
                    continue;
                arr[j] = (l_3 + l_2 + l_1 + arr[j] + n_1 + n_2 + n_3) / 7;
            }
        }
        return arr;
    }
}
