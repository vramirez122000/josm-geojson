package org.openstreetmap.josm.plugins.geojson;

import org.geojson.LineString;
import org.geojson.LngLatAlt;
import org.geojson.Polygon;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.io.FileExporter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GeoJsonFileExporter extends FileExporter {


    /**
     * Constructs a new {@code FileExporter}.
     */
    public GeoJsonFileExporter() {
        super(GeoJsonFileImporter.FILE_FILTER);
    }

    @Override
    public boolean acceptFile(File pathname, Layer layer) {
        if (!(layer instanceof OsmDataLayer)) {
            return false;
        }
        return super.acceptFile(pathname, layer);
    }

    @Override
    public void exportData(File file, Layer layer) throws IOException {
        DataSet data;
        if(layer instanceof GeoJsonLayer) {
            data = ((GeoJsonLayer) layer).getData();
        } else if (layer instanceof OsmDataLayer){
            data = ((OsmDataLayer) layer).data;
        } else {
            throw new IllegalArgumentException("Data layer not supported: " + layer.getClass());
        }

        Collection<Way> ways = data.getWays();

        for (Way way : ways) {

            List<LngLatAlt> coords = new ArrayList<>(way.getNodes().size());
            for (Node n : way.getNodes()) {
                LatLon latLon = n.getCoor();
                LngLatAlt coord = new LngLatAlt(latLon.lon(), latLon.lat());
                coords.add(coord);
            }

            if(way.isClosed()) {
                Polygon polygon = new Polygon();
            } else {
                LineString lineString = new LineString();
            }
        }

    }
}
