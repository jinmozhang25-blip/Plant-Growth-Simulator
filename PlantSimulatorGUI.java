import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PlantSimulatorGUI extends JFrame {
    private SimulationState state;
    private javax.swing.Timer timer;
    private boolean paused = false;
    private boolean isUpdatingSliders = false;

    private JComboBox<String> plantCombo;
    private JSlider tempSlider;
    private JSlider phSlider;
    private JSlider speedSlider;
    private JLabel tempWarningLabel;
    private JLabel phWarningLabel;
    private JLabel dayLabel;
    private JLabel growthRateLabel;
    private JLabel healthLabel;
    private JLabel sizeLabel;
    private JProgressBar progressBar;
    private JButton pauseButton;
    private ChartPanel chartPanel;

    private static final PlantSpecs[] PLANT_SPECS = {
            new PlantSpecs("Lettuce", 4.0, 20.0, 35.0, 5.5, 6.5, 7.5, 0.27, 40.0),
            new PlantSpecs("Tomato", 10.0, 25.0, 35.0, 5.0, 6.0, 7.0, 0.15, 200.0),
            new PlantSpecs("Basil", 15.0, 22.0, 30.0, 5.5, 6.0, 7.5, 0.25, 30.0)
    };

    public PlantSimulatorGUI() {
        setTitle("Plant Growth Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        state = new SimulationState(PLANT_SPECS[0]);

        JPanel leftPanel = createControlPanel();
        JPanel rightPanel = createDashboardPanel();

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        timer = new javax.swing.Timer(1000, e -> {
            if (!paused) {
                state.tick();
                updateUI();
            }
        });
        timer.start();

        setSize(900, 600);
        setLocationRelativeTo(null);
        setVisible(true);
        
        syncSlidersToState();
        updateUI();
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(320, 400));

        JLabel plantLabel = new JLabel("Select Plant");
        plantLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(plantLabel);
        String[] names = new String[PLANT_SPECS.length];
        for (int i = 0; i < PLANT_SPECS.length; i++) names[i] = PLANT_SPECS[i].getName();
        plantCombo = new JComboBox<>(names);
        plantCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        plantCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        plantCombo.addActionListener(e -> changePlant());
        panel.add(plantCombo);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel tempLabel = new JLabel("Temperature");
        tempLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(tempLabel);
        tempSlider = new JSlider(0, 45, 20);
        tempSlider.setMajorTickSpacing(5);
        tempSlider.setPaintTicks(true);
        tempSlider.setPaintLabels(true);
        tempSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        tempSlider.addChangeListener(e -> updateFromSliders());
        panel.add(tempSlider);
        tempWarningLabel = new JLabel(" ");
        tempWarningLabel.setForeground(Color.RED);
        tempWarningLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(tempWarningLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel phLabel = new JLabel("pH Level");
        phLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(phLabel);
        phSlider = new JSlider(0, 14, 6);
        phSlider.setMajorTickSpacing(2);
        phSlider.setPaintTicks(true);
        phSlider.setPaintLabels(true);
        phSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        phSlider.addChangeListener(e -> updateFromSliders());
        panel.add(phSlider);
        phWarningLabel = new JLabel(" ");
        phWarningLabel.setForeground(Color.RED);
        phWarningLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(phWarningLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel speedLabel = new JLabel("Time Speed (×)");
        speedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(speedLabel);
        speedSlider = new JSlider(0, 100, 10);
        speedSlider.setMajorTickSpacing(10);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        speedSlider.addChangeListener(e -> {
            if (!isUpdatingSliders) {
                double speed = speedSlider.getValue() / 10.0;
                state.setSpeed(speed);
                updateUI();
            }
        });
        panel.add(speedSlider);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(e -> togglePause());
        buttonPanel.add(pauseButton);

        JButton resetButton = new JButton("Reset Simulation");
        resetButton.addActionListener(e -> resetSimulation());
        buttonPanel.add(resetButton);

        panel.add(buttonPanel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("0%");
        progressBar.setPreferredSize(new Dimension(200, 30));
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.add(progressBar);
        statsPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel dayTitle = new JLabel("DAY");
        dayTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.add(dayTitle);
        dayLabel = new JLabel("0");
        dayLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.add(dayLabel);
        statsPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel rateTitle = new JLabel("GROWTH RATE");
        rateTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.add(rateTitle);
        growthRateLabel = new JLabel("0.0 g/day");
        growthRateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.add(growthRateLabel);
        statsPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel healthTitle = new JLabel("HEALTH");
        healthTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.add(healthTitle);
        healthLabel = new JLabel("Thriving");
        healthLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.add(healthLabel);
        statsPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        sizeLabel = new JLabel("Size: 0.0 g");
        sizeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.add(sizeLabel);

        chartPanel = new ChartPanel(state);

        panel.add(statsPanel, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    private void togglePause() {
        paused = !paused;
        pauseButton.setText(paused ? "Resume" : "Pause");
    }

    private void changePlant() {
        int idx = plantCombo.getSelectedIndex();
        PlantSpecs newPlant = PLANT_SPECS[idx];
        state.setPlant(newPlant);
        state.reset();
        syncSlidersToState();
        updateUI();
    }

    private void updateFromSliders() {
        if (isUpdatingSliders) return;
        double temp = tempSlider.getValue();
        double ph = phSlider.getValue();
        state.setTemperature(temp);
        state.setPH(ph);
        updateUI();
    }

    private void resetSimulation() {
        state.reset();
        syncSlidersToState();
        updateUI();
    }

    /**
     * Prevents internal layout circular listeners loops while matching slider vectors to data updates
     */
    private void syncSlidersToState() {
        isUpdatingSliders = true;
        tempSlider.setValue((int) Math.round(state.getTemperature()));
        phSlider.setValue((int) Math.round(state.getPH()));
        speedSlider.setValue((int) Math.round(state.getSpeed() * 10));
        isUpdatingSliders = false;
    }

    private void updateUI() {
        PlantSpecs plant = state.getPlant();
        double temp = state.getTemperature();
        double ph = state.getPH();

        if (temp < plant.getTempMin() || temp > plant.getTempMax()) {
            tempWarningLabel.setText(String.format("Outside viable range (%.1f – %.1f °C)",
                    plant.getTempMin(), plant.getTempMax()));
        } else {
            tempWarningLabel.setText(" ");
        }
        if (ph < plant.getPHMin() || ph > plant.getPHMax()) {
            phWarningLabel.setText(String.format("Outside viable range (%.1f – %.1f)",
                    plant.getPHMin(), plant.getPHMax()));
        } else {
            phWarningLabel.setText(" ");
        }

        dayLabel.setText(String.format("%.0f", state.getDay()));
        double rate = state.getGrowthRate();
        growthRateLabel.setText(String.format("%.1f g/day", rate));

        String health = state.getHealthStatus();
        healthLabel.setText(health);
        Color healthColor;
        switch (health) {
            case "Thriving": healthColor = new Color(0, 128, 0); break;
            case "Stressed": healthColor = new Color(255, 165, 0); break;
            case "Dying": healthColor = Color.RED; break;
            case "Dead": healthColor = new Color(128, 0, 0); break;
            default: healthColor = Color.BLACK;
        }
        healthLabel.setForeground(healthColor);

        double size = state.getSize();
        double maxSize = plant.getMaxSize();
        sizeLabel.setText(String.format("Size: %.1f g / %.0f g", size, maxSize));
        int percent = (int) Math.min(100, (size / maxSize) * 100);
        progressBar.setValue(percent);
        progressBar.setString(percent + "%");

        progressBar.setBackground(Color.LIGHT_GRAY);
        if (!state.isAlive()) {
            progressBar.setForeground(new Color(128, 0, 0));
        } else if (health.equals("Thriving")) {
            progressBar.setForeground(new Color(34, 139, 34));
        } else if (health.equals("Stressed")) {
            progressBar.setForeground(new Color(255, 165, 0));
        } else {
            progressBar.setForeground(Color.RED);
        }

        chartPanel.repaint();
    }
}
