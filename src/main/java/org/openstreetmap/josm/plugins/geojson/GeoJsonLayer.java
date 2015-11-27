package org.openstreetmap.josm.plugins.geojson;

import javax.swing.Action;
import javax.swing.Icon;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.visitor.BoundingXYVisitor;
import org.openstreetmap.josm.gui.dialogs.LayerListDialog;
import org.openstreetmap.josm.gui.dialogs.LayerListPopup;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.geojson.DataSetBuilder.BoundedDataSet;
import org.openstreetmap.josm.tools.ImageProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class that displays a map layer containing GeoJSON data.
 *
 * @author Ian Dees <ian.dees@gmail.com>
 * @author matthieun <https://github.com/matthieun>
 */
public class GeoJsonLayer extends OsmDataLayer
{
    private final DataSet data;
    private Bounds bounds = null;

    public GeoJsonLayer(final String name, final BoundedDataSet data, final File associatedFile)
    {
        super(data.getDataSet(), name, null);
        // super(name);
        this.data = data.getDataSet();
        this.bounds = data.getBounds();
        setAssociatedFile(associatedFile);
        setUploadDiscouraged(true);

    }

    public Bounds getBounds()
    {
        return this.bounds;
    }

    public DataSet getData()
    {
        return this.data;
    }

    @Override
    public Icon getIcon()
    {
        return ImageProvider.get("data", "way");
    }

    // @Override
    // public Object getInfoComponent()
    // {
    // // TODO Auto-generated method stub
    // return null;
    // }

    @Override
    public Action[] getMenuEntries()
    {
        final List<Action> actions = new ArrayList<>();
        actions.addAll(Arrays.asList(
                LayerListDialog.getInstance().createActivateLayerAction(this),
                LayerListDialog.getInstance().createShowHideLayerAction(),
                LayerListDialog.getInstance().createDeleteLayerAction(),
                SeparatorLayerAction.INSTANCE,
                LayerListDialog.getInstance().createDuplicateLayerAction(this),
                new LayerSaveAsAction(this),
                SeparatorLayerAction.INSTANCE,
                new LayerListPopup.InfoAction(this)
        ));
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    public String getToolTipText()
    {
        return "GeoJSON";
    }

    @Override
    public boolean isMergable(final Layer other)
    {
        return false;
    }

    // @Override
    // public boolean isModified()
    // {
    // return false;
    // }

    @Override
    public void mergeFrom(final Layer from)
    {
    }

    // @Override
    // public void paint(final Graphics2D graphics, final MapView mapView, final Bounds box)
    // {
    // final Rendering painter = MapRendererFactory.getInstance().createActiveRenderer(graphics,
    // mapView, false);
    // painter.render(this.data, false, box);
    // }

    @Override
    public void visitBoundingBox(final BoundingXYVisitor v)
    {
        v.visit(this.bounds);
    }
}
