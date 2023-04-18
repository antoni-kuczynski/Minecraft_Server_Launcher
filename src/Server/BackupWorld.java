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
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            String dateFormated = "-" + calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH)) +
                    "-" + (calendar.get(Calendar.DAY_OF_MONTH)) + "-" + calendar.get(Calendar.HOUR) + "-" + calendar.get(Calendar.MINUTE) +
                    "-" + calendar.get(Calendar.SECOND) + "-" + calendar.get(Calendar.MILLISECOND);
            FileUtils.copyDirectory(new File(ServerDetails.serverWorldPath),
                    new File(workingDirectory.getAbsolutePath() + "\\" +
                            new File(ServerDetails.serverWorldPath).getName() + dateFormated));
        } catch (IOException e) {
            Frame.alert(AlertType.ERROR, "Cannot backup the server world.\n" + Frame.getErrorDialogMessage(e));
        }
    }
}
