package de.tudresden.cib.vis.runtime.draw2d;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import java.io.File;
import java.io.FileNotFoundException;

public class Draw2DViewer {

    // todo: move to vis.runtime.draw2d

    private Shell shell;
    private FigureCanvas canvas;
    private LightweightSystem ls;
    private Display display;
    private SnapShotParams snapShotParams = new SnapShotParams();

    public Draw2DViewer(){

        display = new Display();
        shell = new Shell(display);
        shell.setLayout(new FillLayout());

        canvas = new FigureCanvas(shell);
        ls = new LightweightSystem(canvas);
    }

    public File chooseFile(String directoryPath, final String fileType) throws FileNotFoundException {
        FileDialog dialog = new FileDialog(shell, SWT.OPEN);
        dialog.setFilterExtensions(new String[]{"*." + fileType});
        dialog.setFilterPath(directoryPath);
        String result = dialog.open();
        return new File(result);
    }

    public Font getDefaultFont(){
        return shell.getFont();
    }

    public void setSnapShotParams(String fileName, int imgType){
        snapShotParams.makeSnap = true;
        snapShotParams.saveImgName = fileName;
        snapShotParams.imgType = imgType;
    }

    private void saveImg(Panel content){
        Image image = new Image(Display.getCurrent(), content.getBounds().width, content.getBounds().height);
        GC gc = new GC(image);
        SWTGraphics graphics = new SWTGraphics(gc);
        content.paint(graphics);
        ImageLoader save = new ImageLoader();
        save.data = new ImageData[] {image.getImageData()};
        save.save(snapShotParams.saveImgName, snapShotParams.imgType);
        image.dispose();
        gc.dispose();

    }

    public void showContent(Panel content){
        canvas.setContents(content);

        ls.setContents(canvas.getViewport());

        shell.open();

        if(snapShotParams.makeSnap){
            saveImg(content);
        }

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) display.sleep();
        }
        display.dispose();


    }

    class SnapShotParams {
        public boolean makeSnap;
        public String saveImgName;
        public int imgType = SWT.IMAGE_PNG;
    }
}
