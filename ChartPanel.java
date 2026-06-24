import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ChartPanel extends JPanel {
    private SimulationState state;

    public ChartPanel(SimulationState state) {
        this.state = state;
        setPreferredSize(new Dimension(400, 200));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Growth Over Time"));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int width = getWidth();
        int height = getHeight();
        int padding = 40;
        int labelPadding = 15;
        int chartWidth = width - 2 * padding;
        int chartHeight = height - 2 * padding - labelPadding;

        List<Double> days = state.getDayHistory();
        List<Double> masses = state.getMassHistory();
        List<String> healths = state.getHealthHistory();

        if (days.size() < 2) {
            g2.setColor(Color.GRAY);
            g2.drawString("Waiting for growth data...", padding + 10, height / 2);
            return;
        }

        g2.setColor(Color.BLACK);
        g2.drawLine(padding, padding, padding, height - padding - labelPadding);
        g2.drawLine(padding, height - padding - labelPadding, width - padding, height - padding - labelPadding);

        g2.drawString("Mass (g)", 5, padding + 10);
        g2.drawString("Time (days)", width - padding - 60, height - 5);

        double minDay = days.get(0);
        double maxDay = days.get(days.size() - 1);
        if (maxDay - minDay < 0.001) {
            g2.drawString("No growth yet", padding + 10, height / 2);
            return;
        }

        double maxMass = state.getPlant().getMaxSize();
        if (maxMass <= 0) maxMass = 1;

        int barWidth = Math.max(2, chartWidth / days.size() - 2);

        for (int i = 0; i < days.size(); i++) {
            double dayVal = days.get(i);
            double massVal = masses.get(i);
            String historicHealth = healths.get(i);

            int x = padding + (int) ((dayVal - minDay) / (maxDay - minDay) * chartWidth);
            int barHeight = (int) ((massVal / maxMass) * chartHeight);
            int y = height - padding - labelPadding - barHeight;

            Color barColor;
            if (historicHealth.equals("Dead")) barColor = Color.DARK_GRAY;
            else if (historicHealth.equals("Thriving")) barColor = new Color(34, 139, 34);
            else if (historicHealth.equals("Stressed")) barColor = new Color(255, 165, 0);
            else barColor = Color.RED; // Dying

            g2.setColor(barColor);
            g2.fillRect(x - barWidth / 2, y, barWidth, barHeight);
        }

        int numTicks = Math.min(10, days.size());
        for (int i = 0; i < numTicks; i++) {
            int index = (i * (days.size() - 1)) / (numTicks - 1);
            double dayVal = days.get(index);
            int x = padding + (int) ((dayVal - minDay) / (maxDay - minDay) * chartWidth);
            g2.drawLine(x, height - padding - labelPadding, x, height - padding - labelPadding + 5);
            g2.drawString(String.format("%.0f", dayVal), x - 10, height - padding - labelPadding + 18);
        }

        for (int i = 0; i <= 4; i++) {
            double val = (maxMass / 4) * i;
            int y = height - padding - labelPadding - (int) ((val / maxMass) * chartHeight);
            g2.drawLine(padding - 5, y, padding, y);
            g2.drawString(String.format("%.1f", val), 2, y + 4);
        }
    }
}
