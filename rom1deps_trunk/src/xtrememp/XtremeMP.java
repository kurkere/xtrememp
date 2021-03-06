/**
 * Xtreme Media Player a cross-platform media player.
 * Copyright (C) 2005-2009 Besmir Beqiri
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package xtrememp;

import com.melloware.jintellitype.IntellitypeListener;
import com.melloware.jintellitype.JIntellitype;
import java.awt.event.ItemEvent;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.SliderUI;
import javax.swing.plaf.basic.BasicSliderUI;
import org.apache.log4j.PropertyConfigurator;
//import org.jdesktop.swingx.JXBusyLabel;
//import org.jdesktop.swingx.util.OS;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.api.SubstanceConstants;
import org.jvnet.substance.painter.decoration.DecorationAreaType;
import org.jvnet.substance.utils.RolloverControlListener;
import org.rom1dep.util.gui.swing.busylabel.AnimationType;
import org.rom1dep.util.gui.swing.busylabel.RDBusyLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xtrememp.player.audio.AudioPlayer;
import xtrememp.player.audio.PlaybackEvent;
import xtrememp.player.audio.PlayerException;
import xtrememp.player.audio.PlaybackListener;
import xtrememp.playlist.Playlist;
import xtrememp.playlist.PlaylistIO;
import xtrememp.playlist.PlaylistItem;
import xtrememp.playlist.PlaylistException;
import xtrememp.ui.button.NextButton;
import xtrememp.ui.button.PlayPauseButton;
import xtrememp.ui.button.PreviousButton;
import xtrememp.ui.button.StopButton;
import xtrememp.ui.button.VolumeButton;
import xtrememp.tag.TagInfo;
import xtrememp.update.SoftwareUpdate;
import xtrememp.update.Version;
import xtrememp.util.AbstractSwingWorker;
import xtrememp.util.file.AudioFileFilter;
import xtrememp.util.LanguageBundle;
import xtrememp.util.log.Log4jProperties;
import xtrememp.util.file.PlaylistFileFilter;
import xtrememp.util.Utilities;

/**
 *
 * @author Besmir Beqiri
 * 
 * Special thanks to rom1dep for the changes applied to this class.
 */
public class XtremeMP implements ActionListener, ControlListener,
        PlaybackListener, IntellitypeListener {

    private static final Logger logger = LoggerFactory.getLogger(XtremeMP.class);
    private final AudioFileFilter audioFileFilter = new AudioFileFilter();
    private final PlaylistFileFilter playlistFileFilter = new PlaylistFileFilter();
    private static XtremeMP instance;
    private JFrame mainFrame;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu playlistMenu;
    private JMenu helpMenu;
    private JMenuItem openMenuItem;
    private JMenuItem openURLMenuItem;
    private JMenuItem openPlaylistMenuItem;
    private JMenuItem savePlaylistMenuItem;
    private JMenuItem preferencesMenuItem;
    private JMenuItem exitMenuItem;
    private JMenuItem playlistMenuItem;
    private JMenuItem nextMenuItem;
    private JMenuItem playPauseMenuItem;
    private JMenuItem stopMenuItem;
    private JMenuItem previousMenuItem;
    private JMenuItem addFilesMenuItem;
    private JMenuItem removeItemsMenuItem;
    private JMenuItem clearPlaylistMenuItem;
    private JMenuItem moveUpItemsMenuItem;
    private JMenuItem moveDownItemsMenuItem;
    private JMenuItem randomizePlaylistMenuItem;
    private JMenuItem infoMenuItem;
    private JMenuItem updateMenuItem;
    private JMenuItem aboutMenuItem;
    private RDBusyLabel busyLabel;
    private JPanel mainPanel;
    private VisualizerPanel visualizationPanel;
    private JPanel controlPanel;
    private AudioPlayer audioPlayer;
    private Playlist playlist;
    private PlaylistManager playlistManager;
    private PreferencesDialog preferencesDialog;
    private StopButton stopButton;
    private PreviousButton previousButton;
    private PlayPauseButton playPauseButton;
    private NextButton nextButton;
    private VolumeButton volumeButton;
    private JLabel timeLabel;
    private JLabel statusLabel;
    private JSlider seekSlider;
    private int oldSeekValue = 0;
    private boolean isSeekPressed = false;
    private JSlider volumeSlider;

    private XtremeMP() {
    }

    public static XtremeMP getInstance() {
        if (instance == null) {
            instance = new XtremeMP();
        }
        return instance;
    }

    public JFrame getMainFrame() {
        return mainFrame;
    }

    public RDBusyLabel getBusyLabel() {
        return busyLabel;
    }

    public void init(List<String> arguments) throws Exception {
        // Process arguments
//        for (String arg : arguments) {
//        }
        audioPlayer = new AudioPlayer(this);
        String mixerName = Settings.getMixerName();
        if (!mixerName.isEmpty()) {
            audioPlayer.setMixerName(mixerName);
        }
        // Launch gui
        EventQueue.invokeAndWait(new Runnable() {

            @Override
            public void run() {
//                if (OS.isWindowsXP()) {
                JFrame.setDefaultLookAndFeelDecorated(true);
                JDialog.setDefaultLookAndFeelDecorated(true);
//                }
                UIManager.put(SubstanceLookAndFeel.FOCUS_KIND, SubstanceConstants.FocusKind.NONE);
                SubstanceLookAndFeel.setSkin(Settings.getSkin());
                LanguageBundle.setLanguage(Locale.getDefault());
                String title = LanguageBundle.getString("Application.title") + " " + Version.getCurrentVersion();
                mainFrame = new JFrame(title);
                mainFrame.setIconImages(Utilities.getIconImages());
                mainFrame.setBounds(Settings.getMainFrameBounds());
                mainFrame.addWindowListener(new WindowAdapter() {

                    @Override
                    public void windowClosing(WindowEvent ev) {
                        exit();
                    }
//                    @Override
//                    public void windowIconified(WindowEvent e) {
//                        if (Toolkit.getDefaultToolkit().isFrameStateSupported(JFrame.ICONIFIED)) {
//                            getMainFrame().setExtendedState(JFrame.ICONIFIED);
//                        }
//                        getMainFrame().setVisible(false);
//                    }
//
//                    @Override
//                    public void windowDeiconified(WindowEvent e) {
//                        if (Toolkit.getDefaultToolkit().isFrameStateSupported(JFrame.NORMAL)) {
//                            getMainFrame().setExtendedState(JFrame.NORMAL);
//                        }
//                        getMainFrame().setVisible(true);
//                    }
                });
                createMenuBar();
                createMainPanels();
                mainFrame.setMinimumSize(new Dimension(controlPanel.getPreferredSize().width + 50, 200));
                if (!Settings.containsKey("xtrememp.mainFrame.x")) {
                    mainFrame.setLocationRelativeTo(null);
                }
                mainFrame.setVisible(true);
            }
        });
        if (JIntellitype.isJIntellitypeSupported()) {
            JIntellitype.getInstance().addIntellitypeListener(this);
        }
        if (Settings.isAutomaticCheckForUpdatesEnabled()) {
            // check for updates after 5 sec
            SoftwareUpdate.scheduleCheckForUpdates(5 * 1000);
        }
        File playlistFile = new File(Settings.getCacheDir(), Utilities.DEFAULT_PLAYLIST);
        if (playlistFile.exists()) {
            playlistManager.loadPlaylist(playlistFile.getAbsolutePath());
        }
        playlist = playlistManager.getPlaylist();
        acOpen();
    }

    public static void main(String[] args) throws Exception {
        List<String> arguments = Arrays.asList(args);
//        boolean debug = arguments.contains("-debug");
        // Load Settings
        Settings.loadSettings();
        // Load log4j properties
        PropertyConfigurator.configure(new Log4jProperties());
        // Enable uncaught exception catching
        try {
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    logger.error(t.getName(), e);
                }
            });
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        }
        // Close error stream
        System.err.close();
        // Init
        getInstance().init(arguments);
    }

    protected void exit() {
        // Save current settings
        Settings.setMainFrameBounds(mainFrame.getBounds());
        Settings.setPlaylistPosition(playlist.getCursorPosition());
        Settings.storeSettings();
        try {
            File playlistFile = new File(Settings.getCacheDir(), Utilities.DEFAULT_PLAYLIST);
            PlaylistIO.saveXSPF(playlist, playlistFile.getAbsolutePath());
        } catch (PlaylistException ex) {
            logger.error("Can't save default playlist", ex);
        }
        // Release system resources
        audioPlayer.stop();
        logger.info("Exit application...");
        System.exit(0);
    }

    protected void createMenuBar() {
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        openMenuItem = new JMenuItem("Open...");
        openURLMenuItem = new JMenuItem("OpenURL...");
        openPlaylistMenuItem = new JMenuItem("Open Playlist...");
        savePlaylistMenuItem = new JMenuItem("Save Playlist...");
        preferencesMenuItem = new JMenuItem("Preferences...");
        exitMenuItem = new JMenuItem("Exit");
        playlistMenu = new JMenu("Playlist");
        playlistMenuItem = new JMenuItem("Show/Hide");
        previousMenuItem = new JMenuItem("Previous");
        playPauseMenuItem = new JMenuItem("Play/Pause");
        stopMenuItem = new JMenuItem("Stop");
        nextMenuItem = new JMenuItem("Next");
        addFilesMenuItem = new JMenuItem("Add files or directories...");
        removeItemsMenuItem = new JMenuItem("Remove selected");
        clearPlaylistMenuItem = new JMenuItem("Clear");
        moveUpItemsMenuItem = new JMenuItem("Move up");
        moveDownItemsMenuItem = new JMenuItem("Move down");
        randomizePlaylistMenuItem = new JMenuItem("Randomize");
        infoMenuItem = new JMenuItem("View info...");
        helpMenu = new JMenu("Help");
        updateMenuItem = new JMenuItem("Check for updates...");
        aboutMenuItem = new JMenuItem("About");

        fileMenu.setMnemonic('F');

        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        openMenuItem.setIcon(Utilities.getIcon("folder.png"));
        openMenuItem.addActionListener(this);
        fileMenu.add(openMenuItem);

        openURLMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_DOWN_MASK));
        openURLMenuItem.setIcon(Utilities.getIcon("folder-remote.png"));
        openURLMenuItem.addActionListener(this);
        fileMenu.add(openURLMenuItem);

        fileMenu.addSeparator();

        openPlaylistMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        openPlaylistMenuItem.setIcon(Utilities.getIcon("document-open.png"));
        openPlaylistMenuItem.addActionListener(this);
        fileMenu.add(openPlaylistMenuItem);

        savePlaylistMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        savePlaylistMenuItem.setIcon(Utilities.getIcon("document-save.png"));
        savePlaylistMenuItem.addActionListener(this);
        fileMenu.add(savePlaylistMenuItem);

        fileMenu.addSeparator();

        preferencesMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
        preferencesMenuItem.addActionListener(this);
        fileMenu.add(preferencesMenuItem);

        fileMenu.addSeparator();

        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        exitMenuItem.addActionListener(this);
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        playlistMenu.setMnemonic('P');

        playlistMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
        playlistMenuItem.addActionListener(this);
        playlistMenu.add(playlistMenuItem);

        playlistMenu.addSeparator();

        previousMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        previousMenuItem.addActionListener(this);
        playlistMenu.add(previousMenuItem);

        playPauseMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        playPauseMenuItem.addActionListener(this);
        playlistMenu.add(playPauseMenuItem);

        stopMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK));
        stopMenuItem.addActionListener(this);
        playlistMenu.add(stopMenuItem);

        nextMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        nextMenuItem.addActionListener(this);
        playlistMenu.add(nextMenuItem);

        playlistMenu.addSeparator();

        addFilesMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0));
        addFilesMenuItem.setIcon(Utilities.getIcon("list-add.png"));
        addFilesMenuItem.addActionListener(this);
        playlistMenu.add(addFilesMenuItem);

        removeItemsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        removeItemsMenuItem.setIcon(Utilities.getIcon("list-remove.png"));
        removeItemsMenuItem.addActionListener(this);
        playlistMenu.add(removeItemsMenuItem);

        clearPlaylistMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.ALT_DOWN_MASK));
        clearPlaylistMenuItem.setIcon(Utilities.getIcon("edit-clear.png"));
        clearPlaylistMenuItem.addActionListener(this);
        playlistMenu.add(clearPlaylistMenuItem);

        moveUpItemsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.ALT_DOWN_MASK));
        moveUpItemsMenuItem.setIcon(Utilities.getIcon("go-up.png"));
        moveUpItemsMenuItem.addActionListener(this);
        playlistMenu.add(moveUpItemsMenuItem);

        moveDownItemsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.ALT_DOWN_MASK));
        moveDownItemsMenuItem.setIcon(Utilities.getIcon("go-down.png"));
        moveDownItemsMenuItem.addActionListener(this);
        playlistMenu.add(moveDownItemsMenuItem);

        randomizePlaylistMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
        randomizePlaylistMenuItem.addActionListener(this);
        playlistMenu.add(randomizePlaylistMenuItem);

        infoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));
        infoMenuItem.addActionListener(this);
        playlistMenu.add(infoMenuItem);

        menuBar.add(playlistMenu);

        helpMenu.setMnemonic('H');

        updateMenuItem.addActionListener(this);
        helpMenu.add(updateMenuItem);
        helpMenu.addSeparator();

        aboutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.ALT_DOWN_MASK));
        aboutMenuItem.addActionListener(this);
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        menuBar.add(Box.createHorizontalGlue());

        busyLabel = new RDBusyLabel(new Dimension(22, 22));
        busyLabel.loadBaseImage(Utilities.getImage("logo_alt_base.png"));
        busyLabel.loadAnimImage(Utilities.getImage("logo_alt.png"));
        busyLabel.setAnimationType(AnimationType.ROTATE_3D);
        busyLabel.initImages();
        busyLabel.setVisible(true);
        menuBar.add(busyLabel);

        menuBar.add(Box.createHorizontalStrut(5));

        mainFrame.setJMenuBar(menuBar);
    }

    protected void createMainPanels() {
        mainPanel = new JPanel(new CardLayout());
        playlistManager = new PlaylistManager(this);
        visualizationPanel = new VisualizerPanel();
        if (Settings.getLastView().equals(Utilities.VISUALIZATION_PANEL)) {
            mainPanel.add(visualizationPanel, Utilities.VISUALIZATION_PANEL);
            mainPanel.add(playlistManager, Utilities.PLAYLIST_MANAGER);
            audioPlayer.getDspAudioDataConsumer().add(visualizationPanel);
        } else {
            mainPanel.add(playlistManager, Utilities.PLAYLIST_MANAGER);
            mainPanel.add(visualizationPanel, Utilities.VISUALIZATION_PANEL);
        }
        mainFrame.getContentPane().add(mainPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        SubstanceLookAndFeel.setDecorationType(southPanel, DecorationAreaType.TOOLBAR);
        seekSlider = new JSlider(0, 0, 0);
        seekSlider.setBorder(new EmptyBorder(1, 5, 0, 5));
        for (MouseListener ml : seekSlider.getMouseListeners()) {
            if (!(ml instanceof RolloverControlListener)) {
                seekSlider.removeMouseListener(ml);
            }
        }
        for (MouseMotionListener mml : seekSlider.getMouseMotionListeners()) {
            if (!(mml instanceof RolloverControlListener)) {
                seekSlider.removeMouseMotionListener(mml);
            }
        }
        seekSlider.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                isSeekPressed = true;
                SliderUI sliderUI = seekSlider.getUI();
                if (seekSlider.isEnabled() && sliderUI instanceof BasicSliderUI) {
                    BasicSliderUI basicSliderUI = (BasicSliderUI) sliderUI;
                    if (seekSlider.getOrientation() == JSlider.HORIZONTAL) {
                        seekSlider.setValue(basicSliderUI.valueForXPosition(e.getX()));
                    } else {
                        seekSlider.setValue(basicSliderUI.valueForYPosition(e.getY()));
                    }
                    PlaylistItem pli = playlist.getCursor();
                    if (pli != null) {
                        oldSeekValue = seekSlider.getValue();
                        updateTime(pli, oldSeekValue);
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                try {
                    if (audioPlayer != null && seekSlider.isEnabled()) {
                        audioPlayer.seek(Math.round(audioPlayer.getByteLength() * (double) oldSeekValue / seekSlider.getMaximum()));
                    }
                } catch (PlayerException ex) {
                    logger.error(ex.getMessage(), ex);
                }
                isSeekPressed = false;
            }
        });
        seekSlider.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                SliderUI sliderUI = seekSlider.getUI();
                if (seekSlider.isEnabled() && sliderUI instanceof BasicSliderUI) {
                    BasicSliderUI basicSliderUI = (BasicSliderUI) sliderUI;
                    if (seekSlider.getOrientation() == JSlider.HORIZONTAL) {
                        seekSlider.setValue(basicSliderUI.valueForXPosition(e.getX()));
                    } else {
                        seekSlider.setValue(basicSliderUI.valueForYPosition(e.getY()));
                    }
                    PlaylistItem pli = playlist.getCursor();
                    if (pli != null) {
                        oldSeekValue = seekSlider.getValue();
                        updateTime(pli, oldSeekValue);
                    }
                }
            }
        });
        seekSlider.setEnabled(false);
        southPanel.add(seekSlider, BorderLayout.NORTH);

        controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        stopButton = new StopButton();
        stopButton.addActionListener(this);
        controlPanel.add(stopButton);
        previousButton = new PreviousButton();
        previousButton.addActionListener(this);
        controlPanel.add(previousButton);
        playPauseButton = new PlayPauseButton();
        playPauseButton.addActionListener(this);
        controlPanel.add(playPauseButton);
        nextButton = new NextButton();
        nextButton.addActionListener(this);
        controlPanel.add(nextButton);
        volumeButton = new VolumeButton(Settings.isMuted());
        JPopupMenu volumePopupMenu = volumeButton.getPopupMenu();
        volumeSlider = new JSlider(JSlider.VERTICAL, 0, 100, Settings.getGain());
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        volumeSlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                Object source = e.getSource();
                if (source == volumeSlider) {
                    if (volumeSlider.getValueIsAdjusting()) {
                        try {
                            int volumeValue = volumeSlider.getValue();
                            volumeButton.setVolumeIcon(volumeValue);
                            audioPlayer.setGain(volumeValue / 100.0F);
                            Settings.setGain(volumeValue);
                        } catch (PlayerException ex) {
                            logger.debug(ex.getMessage(), ex);
                        }
                    }
                }
            }
        });
        volumeSlider.setEnabled(!Settings.isMuted());
        JPanel volumePanel = new JPanel(new BorderLayout());
        JLabel volumeLabel = new JLabel("Volume", JLabel.CENTER);
        volumeLabel.setFont(volumeLabel.getFont().deriveFont(Font.BOLD));
        volumePanel.add(volumeLabel, BorderLayout.NORTH);
        volumePanel.add(volumeSlider, BorderLayout.CENTER);
        JCheckBox muteCheckBox = new JCheckBox("Mute");
        muteCheckBox.setSelected(Settings.isMuted());
        muteCheckBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                try {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        volumeSlider.setEnabled(false);
                        volumeButton.setVolumeMutedIcon();
                        audioPlayer.setMuted(true);
                        Settings.setMuted(true);
                    } else {
                        volumeSlider.setEnabled(true);
                        volumeButton.setVolumeIcon(Settings.getGain());
                        audioPlayer.setMuted(false);
                        Settings.setMuted(false);
                    }
                } catch (PlayerException ex) {
                    logger.debug(ex.getMessage(), ex);
                }
            }
        });
        volumePanel.add(muteCheckBox, BorderLayout.SOUTH);
        volumePopupMenu.add(volumePanel);
        controlPanel.add(volumeButton);
        controlPanel.setBorder(new EmptyBorder(0, 0, 5, 0));
        southPanel.add(controlPanel, BorderLayout.CENTER);

        JPanel statusBar = new JPanel(new BorderLayout());
        SubstanceLookAndFeel.setDecorationType(statusBar, DecorationAreaType.FOOTER);
        statusBar.setBorder(new EmptyBorder(3, 0, 3, 0));
        timeLabel = new JLabel(Utilities.ZERO_TIMER);
        timeLabel.setFont(timeLabel.getFont().deriveFont(Font.BOLD));
        timeLabel.setBorder(new EmptyBorder(0, 10, 0, 5));
        statusBar.add(timeLabel, BorderLayout.WEST);
        statusLabel = new JLabel();
        statusLabel.setBorder(new EmptyBorder(0, 5, 0, 10));
        statusBar.add(statusLabel, BorderLayout.CENTER);
        southPanel.add(statusBar, BorderLayout.SOUTH);
        mainFrame.getContentPane().add(southPanel, BorderLayout.SOUTH);
    }

    protected void switchView() {
        CardLayout cardLayout = (CardLayout) (mainPanel.getLayout());
        if (playlistManager.isVisible()) {
            cardLayout.show(mainPanel, Utilities.VISUALIZATION_PANEL);
            Settings.setLastView(Utilities.VISUALIZATION_PANEL);
            audioPlayer.getDspAudioDataConsumer().add(visualizationPanel);
        } else {
            audioPlayer.getDspAudioDataConsumer().remove(visualizationPanel);
            cardLayout.show(mainPanel, Utilities.PLAYLIST_MANAGER);
            Settings.setLastView(Utilities.PLAYLIST_MANAGER);
        }
    }

    protected void updateTime(PlaylistItem pli, final int value) {
        String formattedLength = pli.getFormattedLength();
        final String timeText;
        if (Utilities.isNullOrEmpty(formattedLength)) {
            timeText = pli.getFormattedLength(Math.round(value / 1000f));
        } else {
            timeText = pli.getFormattedLength(Math.round(value / 1000f)) + " / " + formattedLength.trim();
        }
        setTime(timeText, value);
    }

    protected void setTime(final String timeText, final int seekSliderValue) {
        if (EventQueue.isDispatchThread()) {
            seekSlider.setValue(seekSliderValue);
            timeLabel.setText(timeText);
        } else {
            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    seekSlider.setValue(seekSliderValue);
                    timeLabel.setText(timeText);
                }
            });
        }
    }

    protected void setStatus(final String text) {
        if (EventQueue.isDispatchThread()) {
            statusLabel.setText(text);
        } else {
            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    statusLabel.setText(text);
                }
            });
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == openMenuItem) {
            JFileChooser fileChooser = new JFileChooser(Settings.getLastDir());
            fileChooser.addChoosableFileFilter(playlistFileFilter);
            fileChooser.addChoosableFileFilter(audioFileFilter);
            fileChooser.setMultiSelectionEnabled(false);
            if (fileChooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                FileFilter fileFilter = fileChooser.getFileFilter();
                if (fileFilter == playlistFileFilter) {
                    playlistManager.clearPlaylist();
                    acStop();
                    playlistManager.loadPlaylist(file.getPath());
                } else if (fileFilter == audioFileFilter) {
                    String fileName = file.getName().substring(0, file.getName().lastIndexOf(".")).trim();
                    PlaylistItem pli = new PlaylistItem(fileName, file.getAbsolutePath(), -1, true);
                    playlistManager.add(pli);
                    playlist.setCursor(playlist.indexOf(pli));
                }
                acOpenAndPlay();
                Settings.setLastDir(file.getParent());
            }
        } else if (source == openURLMenuItem) {
            String url = JOptionPane.showInputDialog(mainFrame, "Enter the URL to a media file on the Internet!", "Open URL", JOptionPane.QUESTION_MESSAGE);
            if (url != null && Utilities.startWithProtocol(url)) {
                boolean isPlaylistFile = false;
                for (String ext : PlaylistFileFilter.playlistExt) {
                    if (url.endsWith(ext)) {
                        isPlaylistFile = true;
                    }
                }
                if (isPlaylistFile) {
                    playlistManager.clearPlaylist();
                    playlistManager.loadPlaylist(url);
                    playlist.begin();
                } else {
                    PlaylistItem pli = new PlaylistItem(url, url, -1, false);
                    playlistManager.add(pli);
                    playlist.setCursor(playlist.indexOf(pli));
                }
                acOpenAndPlay();
            }
        } else if (source == openPlaylistMenuItem) {
            playlistManager.openPlaylist();
        } else if (source == savePlaylistMenuItem) {
            playlistManager.savePlaylistDialog();
        } else if (source == preferencesMenuItem) {
            preferencesDialog = new PreferencesDialog(mainFrame, audioPlayer);
            preferencesDialog.setVisible(true);
        } else if (source == exitMenuItem) {
            exit();
        } else if (source == playlistMenuItem) {
            switchView();
        } else if (source == playPauseMenuItem || source == playPauseButton) {
            if (playlist.isEmpty() && !audioPlayer.isPlaying()) {
                playlistManager.addFilesDialog();
            } else {
                acPlayPause();
            }
        } else if (source == previousMenuItem || source == previousButton) {
            acPrevious();
        } else if (source == nextMenuItem || source == nextButton) {
            acNext();
        } else if (source == addFilesMenuItem) {
            playlistManager.addFilesDialog();
        } else if (source == removeItemsMenuItem) {
            playlistManager.remove();
        } else if (source == clearPlaylistMenuItem) {
            playlistManager.clearPlaylist();
            //acStop();
        } else if (source == moveUpItemsMenuItem) {
            playlistManager.moveUp();
        } else if (source == moveDownItemsMenuItem) {
            playlistManager.moveDown();
        } else if (source == randomizePlaylistMenuItem) {
            playlistManager.randomizePlaylist();
            //acOpen();
        } else if (source == stopMenuItem || source == stopButton) {
            acStop();
        } else if (source == infoMenuItem) {
            playlistManager.showMediaInfoDialog();
        } else if (source == updateMenuItem) {
            SoftwareUpdate.checkForUpdates(true, false);
            SoftwareUpdate.showCheckForUpdatesDialog();
        } else if (source == aboutMenuItem) {
            Object[] options = {LanguageBundle.getString("Button.Close")};
            Desktop desktop = null;
            if (Desktop.isDesktopSupported()) {
                desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    options = new Object[]{LanguageBundle.getString("Button.Close"), LanguageBundle.getString("Button.Website")};
                }
            }
            Version currentVersion = Version.getCurrentVersion();
            StringBuffer message = new StringBuffer();
            message.append("<html><b><font color='red' size='5'>" + LanguageBundle.getString("Application.title"));
            message.append("</font></b><br>" + LanguageBundle.getString("Application.description"));
            message.append("<br>Copyright © 2005-2009 The Xtreme Media Player Project");
            message.append("<br><br><b>Author: </b>" + LanguageBundle.getString("Application.author"));
            message.append("<br><b>Version: </b>" + currentVersion);
            message.append("<br><b>Release date: </b>" + currentVersion.getReleaseDate());
            message.append("<br><b>Homepage: </b>" + LanguageBundle.getString("Application.homepage"));
            message.append("<br><br><b>Java version: </b>" + System.getProperty("java.version"));
            message.append("<br><b>Java vendor: </b>" + System.getProperty("java.vendor"));
            message.append("<br><b>Java home: </b>" + System.getProperty("java.home"));
            message.append("<br><b>OS name: </b>" + System.getProperty("os.name"));
            message.append("<br><b>OS arch: </b>" + System.getProperty("os.arch"));
            message.append("<br><b>User name: </b>" + System.getProperty("user.name"));
            message.append("<br><b>User home: </b>" + System.getProperty("user.home"));
            message.append("<br><b>User dir: </b>" + System.getProperty("user.dir"));
            message.append("</html>");
            int n = JOptionPane.showOptionDialog(mainFrame, message, "About", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if (n == 1 && desktop != null) {
                try {
                    URL url = new URL(LanguageBundle.getString("Application.homepage"));
                    desktop.browse(url.toURI());
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        }
    }

    @Override
    public void playbackBuffering(PlaybackEvent pe) {
        setStatus(LanguageBundle.getString("MainFrame.StatusBar.Buffering"));
    }

    @Override
    public void playbackOpened(PlaybackEvent pe) {
        try {
            audioPlayer.setGain(Settings.getGain() / 100.0F);
            audioPlayer.setMuted(Settings.isMuted());
        } catch (PlayerException ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            PlaylistItem pli = playlist.getCursor();
            if (pli != null && !pli.isFile()) {
                pli.loadTagInfo();
            }
        }
    }

    @Override
    public void playbackEndOfMedia(PlaybackEvent pe) {
        acNext();
    }

    @Override
    public void playbackPlaying(PlaybackEvent pe) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                playPauseButton.setPauseIcon();
            }
        });
    }

    @Override
    public void playbackProgress(PlaybackEvent pe) {
        final PlaylistItem pli = playlist.getCursor();
        if (pli != null && !seekSlider.getValueIsAdjusting() && !isSeekPressed && mainFrame.isVisible()) {
            int position = oldSeekValue + Math.round(pe.getPosition() / 1000f);
            updateTime(pli, position);

            // Shoutcast stream title.
            Map properties = pe.getProperties();
            if (!pli.isFile() && properties.containsKey("mp3.shoutcast.metadata.StreamTitle")) {
                String streamTitle = ((String) properties.get("mp3.shoutcast.metadata.StreamTitle")).trim();
                TagInfo tagInfo = pli.getTagInfo();
                if (!streamTitle.isEmpty() && (tagInfo != null)) {
                    String sTitle = " (" + tagInfo.getTitle() + ")";
                    if (!pli.getFormattedDisplayName().equals(streamTitle + sTitle)) {
                        pli.setFormattedDisplayName(streamTitle + sTitle);
                        EventQueue.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                playlistManager.refreshRow(playlist.indexOf(pli));
                                setStatus(pli.getFormattedDisplayName());
                            }
                        });
                    }
                }
            }
        }
    }

    @Override
    public void playbackPaused(PlaybackEvent pe) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                playPauseButton.setPlayIcon();
            }
        });
    }

    @Override
    public void playbackStopped(PlaybackEvent pe) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                playPauseButton.setPlayIcon();
                if (oldSeekValue == 0) {
                    setTime(Utilities.ZERO_TIMER, 0);
                }
            }
        });
    }

    @Override
    public void acOpen() {
        PlayerLauncher playerLauncher = new PlayerLauncher(false);
        playerLauncher.execute();
    }

    @Override
    public void acOpenAndPlay() {
        PlayerLauncher playerLauncher = new PlayerLauncher(true);
        playerLauncher.execute();
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                seekSlider.setEnabled(true);
            }
        });
    }

    @Override
    public void acPrevious() {
        if (!playlist.isEmpty()) {
            playlist.previousCursor();
            acOpenAndPlay();
        }
    }

    @Override
    public void acNext() {
        if (!playlist.isEmpty()) {
            playlist.nextCursor();
            acOpenAndPlay();
        }
    }

    @Override
    public void acPlayPause() {
        if (EventQueue.isDispatchThread()) {
            if (!seekSlider.isEnabled()) {
                seekSlider.setEnabled(true);
            }
        } else {
            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    if (!seekSlider.isEnabled()) {
                        seekSlider.setEnabled(true);
                    }
                }
            });
        }
        try {
            if (audioPlayer.isPlaying()) {
                audioPlayer.pause();
            } else {
                audioPlayer.play();
            }
//                switch (audioPlayer.getState()) {
//                    case AudioPlayer.PLAY:
//                        audioPlayer.pause();
//                        break;
//                    case AudioPlayer.STOP:
//                        acOpenAndPlay();
//                        break;
//                    default:
//                        audioPlayer.play();
//                        break;
//                }
            } catch (PlayerException ex) {
            logger.error(ex.getMessage());
//                String msg = "<html><b>An exeption was generated:</b><br><br>" + ex.getMessage() + "<html>";
//                JOptionPane.showMessageDialog(mainFrame, msg, "Error", JOptionPane.ERROR_MESSAGE);

        }
    }

    @Override
    public void acStop() {
        oldSeekValue = 0;
        audioPlayer.stop();
    }

    @Override
    public void acDisable() {
        if (EventQueue.isDispatchThread()) {
            seekSlider.setEnabled(false);
            statusLabel.setText("");
            timeLabel.setText("playing back...");
        } else {
            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    seekSlider.setEnabled(false);
                    statusLabel.setText("");
                    timeLabel.setText("playing back...");
                }
            });
        }
    }

    @Override
    public void onIntellitype(int command) {
        switch (command) {
            case JIntellitype.APPCOMMAND_MEDIA_PLAY_PAUSE:
                acPlayPause();
                break;
            case JIntellitype.APPCOMMAND_MEDIA_PREVIOUSTRACK:
                acPrevious();
                break;
            case JIntellitype.APPCOMMAND_MEDIA_NEXTTRACK:
                acNext();
                break;
            case JIntellitype.APPCOMMAND_MEDIA_STOP:
                acStop();
                break;
        }
    }

    private class PlayerLauncher extends AbstractSwingWorker<Boolean, Void> {

        private PlaylistItem pli = null;
        private boolean play = false;
        private boolean isFile = false;
        private int duration = -1;

        public PlayerLauncher(boolean play) {
            this.pli = playlist.getCursor();
            this.play = play;
        }

        @Override
        protected Boolean doInBackground() throws PlayerException, MalformedURLException {
            if (pli != null) {
                isFile = pli.isFile();
                if (isFile) {
                    audioPlayer.open(new File(pli.getLocation()));
                    duration = Math.round(audioPlayer.getDuration() / 1000);
                } else {
                    audioPlayer.open(new URL(pli.getLocation()));
                }
                if (play) {
                    audioPlayer.play();
                }
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }

        @Override
        protected void done() {
            oldSeekValue = 0;
            seekSlider.setValue(0);
            seekSlider.setMinimum(0);
            seekSlider.setMaximum(0);
            try {
                if (get()) {
                    playlistManager.colorizeRow();
                    if (isFile) {
                        if (duration > 0) {
                            seekSlider.setMaximum(duration);
                            seekSlider.setEnabled(true);
                        } else {
                            seekSlider.setMaximum((int) (pli.getDuration() * 1000));
                            //seekSlider.setEnabled(false);
                        }
                    } else {
                        seekSlider.setEnabled(false);
                    }
                    setStatus(pli.getFormattedDisplayName());
                }
            } catch (Exception ex) {
                if (ex.getCause() instanceof PlayerException) {
                    acStop();
                    setStatus("An error occurred...");
                    logger.error(ex.getMessage(), ex);
//                    String msg = "<html><b>An exeption was generated:</b><br><br>" + ex.getMessage() + "<html>";
//                    JOptionPane.showMessageDialog(mainFrame, msg, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
