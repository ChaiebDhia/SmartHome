package com.smarthome.ui;

import com.smarthome.model.Home;
import com.smarthome.model.SmartDevice;
import com.smarthome.model.Room;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SwingDashboard extends JFrame {
    private final Home home;
    private final DefaultTableModel model;

    public SwingDashboard(Home home) {
        super("Smart Home Dashboard");
        this.home = home;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 500);
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new Object[]{"Room","Device","Type","Status","Power (W)"},0);
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField deviceField = new JTextField(12);
        JButton onBtn = new JButton("ON");
        JButton offBtn = new JButton("OFF");
        JButton toggleBtn = new JButton("TOGGLE");
        JButton refreshBtn = new JButton("Refresh");

        onBtn.addActionListener(e -> act(deviceField.getText(), "on"));
        offBtn.addActionListener(e -> act(deviceField.getText(), "off"));
        toggleBtn.addActionListener(e -> act(deviceField.getText(), "toggle"));
        refreshBtn.addActionListener(this::refreshAction);

        controls.add(new JLabel("Device:"));
        controls.add(deviceField);
        controls.add(onBtn);
        controls.add(offBtn);
        controls.add(toggleBtn);
        controls.add(refreshBtn);
        add(controls, BorderLayout.NORTH);

        refresh();
    }

    private void act(String name, String action) {
        try {
            SmartDevice d = home.getDeviceByName(name);
            switch (action) {
                case "on": d.turnOn(); break;
                case "off": d.turnOff(); break;
                case "toggle": d.toggle(); break;
            }
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshAction(ActionEvent e) { refresh(); }

    private void refresh() {
        model.setRowCount(0);
        for (Room r : home.getRooms()) {
            for (SmartDevice d : r.getDevices()) {
                model.addRow(new Object[]{r.getName(), d.getName(), d.getType(), d.getStatus(), String.format("%.1f", d.getCurrentPowerConsumption())});
            }
        }
    }

    public static void launch(Home home) {
        SwingUtilities.invokeLater(() -> new SwingDashboard(home).setVisible(true));
    }
}
