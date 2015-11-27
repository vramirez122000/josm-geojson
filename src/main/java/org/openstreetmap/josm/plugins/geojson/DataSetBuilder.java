package org.openstreetmap.josm.plugins.geojson;

import org.geojson.*;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author matthieun
 */
public class DataSetBuilder
{
    /**
     * @author matthieun
     */
    public static class BoundedDataSet
    {
        private final DataSet dataSet;
        private final Bounds bounds;

        /**
         *
         * @param dataSet
         * @param bounds
         */
        public BoundedDataSet(final DataSet dataSet, final Bounds bounds)
        {
            this.dataSet = dataSet;
            this.bounds = bounds;
        }

        public Bounds getBounds()
        {
            return this.bounds;
        }

        public DataSet getDataSet()
        {
            return this.dataSet;
        }
    }

    /**
     *
     * @param geoJson geoJson Object
     * @return
     */
    public BoundedDataSet build(final GeoJsonObject geoJson)
    {

        final MutableBoundedDataSet data = new MutableBoundedDataSet();
        if (!(geoJson instanceof FeatureCollection)) {
            throw new UnsupportedOperationException("Only FeatureCollection is supported at the moment!");
        }

        final FeatureCollection fc = (FeatureCollection) geoJson;
        for (Feature feature : fc) {

            final GeoJsonObject geometry = feature.getGeometry();
            if(geometry instanceof Point) {
                Node n = convertLngLatAlt(((Point) geometry).getCoordinates(), data);
                n.setKeys(getTags(feature.getProperties()));
                data.dataSet.addPrimitive(n);
                continue;
            }

            if(geometry instanceof MultiPoint) {

                if (geometry instanceof LineString) {
                    Way w = convertCoordinatesToWay(((MultiPoint) geometry).getCoordinates(), data);
                    w.setKeys(getTags(feature.getProperties()));
                    data.dataSet.addPrimitive(w);
                    continue;
                }

                for (LngLatAlt lngLatAlt : ((MultiPoint) geometry).getCoordinates()) {
                    Node n = convertLngLatAlt(lngLatAlt, data);
                    n.setKeys(getTags(feature.getProperties()));
                    data.dataSet.addPrimitive(n);
                    continue;
                }


            }

            if (geometry instanceof Polygon) {

                final List<List<LngLatAlt>> rings = ((Polygon) geometry).getCoordinates();
                if(rings.size() == 1) {
                    Way w = convertCoordinatesToWay(rings.get(0), data);
                    w.setKeys(getTags(feature.getProperties()));
                    data.dataSet.addPrimitive(w);
                    continue;
                }

                Relation r = new Relation();

                for (int i = 0; i < rings.size(); i++) {
                    final List<LngLatAlt> ring = rings.get(i);

                    Way w = convertCoordinatesToWay(ring, data);
                    data.dataSet.addPrimitive(w);
                    if(i == 0) {
                        r.addMember(new RelationMember("outer", w));
                    } else {
                        r.addMember(new RelationMember("inner", w));
                    }
                }

                final Map<String, String> tags = getTags(feature.getProperties());
                tags.put("type", "multipolygon");
                r.setKeys(tags);
                data.dataSet.addPrimitive(r);
                continue;



            }

            throw new IllegalArgumentException(" Only Point, Polygon, Linestring and MultiPoint are supported");

        }

        return new BoundedDataSet(data.dataSet, data.bounds);
    }

    private Way convertCoordinatesToWay(final List<LngLatAlt> coordinates, final MutableBoundedDataSet d)
    {
        Way w = new Way();
        final List<Node> nodes = new ArrayList<>(coordinates.size());
        for (LngLatAlt lngLatAlt : coordinates) {
            Node n = convertLngLatAlt(lngLatAlt, d);
            d.dataSet.addPrimitive(n);
            nodes.add(n);
        }
        w.setNodes(nodes);
        return w;
    }

    private Node convertLngLatAlt(final LngLatAlt lngLatAlt, final MutableBoundedDataSet d)
    {
        final LatLon latlon = new LatLon(lngLatAlt.getLatitude(), lngLatAlt.getLongitude());

        if (d.bounds == null) {
            d.bounds = new Bounds(latlon);
        } else {
            d.bounds.extend(latlon);
        }

        return new Node(latlon);

    }

    private Map<String, String> getTags(final Map<String, Object> properties)
    {
        final Map<String, String> tags = new TreeMap<>();
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            tags.put(entry.getKey(), entry.getValue().toString());
        }
        return tags;
    }

    private class MutableBoundedDataSet {
        DataSet dataSet = new DataSet();
        Bounds bounds;
    }
}
