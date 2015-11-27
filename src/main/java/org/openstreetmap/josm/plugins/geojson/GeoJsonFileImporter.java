package org.openstreetmap.josm.plugins.geojson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.GeoJsonObject;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.ExtensionFileFilter;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.gui.progress.ProgressMonitor;
import org.openstreetmap.josm.gui.util.GuiHelper;
import org.openstreetmap.josm.io.FileImporter;
import org.openstreetmap.josm.plugins.geojson.DataSetBuilder.BoundedDataSet;

import java.io.File;
import java.io.IOException;

import static org.openstreetmap.josm.tools.I18n.tr;

/**
 * @author Ian Dees <ian.dees@gmail.com>
 * @author matthieun <https://github.com/matthieun>
 */
public class GeoJsonFileImporter extends FileImporter
{
    public static final ExtensionFileFilter FILE_FILTER = new ExtensionFileFilter("geojson,json", "geojson", tr("GeoJSON file") + " (*.geojson,*.json)");

    private GeoJsonLayer layer = null;

    public GeoJsonFileImporter() {
        super(FILE_FILTER);
    }


    public GeoJsonLayer getLayer()
    {
        return this.layer;
    }

    @Override
    public void importData(final File file, final ProgressMonitor progressMonitor)
    {
        GeoJsonObject object;
        System.out.println("Parsing GeoJSON: " + file.getAbsolutePath());
        try
        {
            object = new ObjectMapper().readValue(file, GeoJsonObject.class);
            System.out.println("Found: " + object.getClass());
        } catch (final IOException e)
        {
            throw new IllegalArgumentException("Could not parse JSON", e);
        }
        final BoundedDataSet data = new DataSetBuilder().build(object);
        this.layer = new GeoJsonLayer("GeoJSON: " + file.getName(), data, file);

        GuiHelper.runInEDT(new Runnable() {
            @Override
            public void run() {
                Main.main.addLayer(GeoJsonFileImporter.this.layer);
                System.out.println("Added layer.");
            }
        });
    }
}
