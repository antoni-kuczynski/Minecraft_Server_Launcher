package Server;

import Gui.AlertType;
import Gui.Frame;
import SelectedServer.ServerDetails;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class BackupWorld extends Thread {
    public BackupWorld() {

    }

    @Override
    public void run() {
        File workingDirectory = new File(ServerDetails.serverPath + "\\server_launcher_world_backups\\");
        if(!workingDirectory.exists())
            workingDirectory.mkdirs();
        try {
            Date date = new Date(); //fuck american date format btw
            String dateFormated = "-" + date.getDate() + "-" + (date.getMonth()+1) +
                    "-" + (date.getYear()+1900) + "-" + date.getHours() + "-" + date.getMinutes() + "-" + date.getSeconds();
            FileUtils.copyDirectory(new File(ServerDetails.serverWorldPath),
                    new File(workingDirectory.getAbsolutePath() + "\\" +
                            new File(ServerDetails.serverWorldPath).getName() + dateFormated));
        } catch (IOException e) {
            Frame.alert(AlertType.ERROR, "Cannot backup the server world.\n" + Frame.getErrorDialogMessage(e));
        }
    }
}
