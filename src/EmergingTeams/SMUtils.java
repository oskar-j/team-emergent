package EmergingTeams;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.query.space.grid.GridCell;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.GridPoint;

public final strictfp class SMUtils {
        public static <T> T randomElementOf(final List<T> list) {
                return list.get(RandomHelper.nextIntFromTo(0, list.size() - 1));
        }

        public static <T> List<GridCell<T>> getFreeGridCells(
                        final List<GridCell<T>> neighborhood) {
                final ArrayList<GridCell<T>> ret = new ArrayList<GridCell<T>>();

                for (final GridCell<T> act : neighborhood) {
                        if (0 == act.size()) {
                                ret.add(act);
                        }
                }

                return ret;
        }
}