package de.hsbi.lockgame.ui;

import de.hsbi.lockgame.logic.GameEngine;
import de.hsbi.lockgame.logic.GameState;
import de.hsbi.lockgame.logic.GameStateListener;
import de.hsbi.lockgame.model.Direction;
import de.hsbi.lockgame.settings.GameConstants;
import de.hsbi.lockgame.settings.InputConstants;
import de.hsbi.lockgame.ui.render.GameRenderer;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GamePanel extends JPanel implements GameStateListener {

    private GameState state;
    private final GameRenderer renderer;
    private GameEngine gameEngine;

    // 🔥 WICHTIG: exakt wie vorher (Main nutzt diesen Konstruktor)
    public GamePanel(GameState initialState, GameRenderer renderer) {
        this.state = initialState;
        this.renderer = renderer;

        var width = initialState.level().width() * GameConstants.TILE_SIZE;
        var height = initialState.level().height() * GameConstants.TILE_SIZE;

        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);

        setFocusable(true);
        InputConstants.BINDINGS.forEach(this::setupKeyBindings);
    }

    @Override
    public void onStateChanged(GameState newState) {
        this.state = newState;
        repaint();
    }

    public void setGameEngine(GameEngine engine) {
        this.gameEngine = engine;
        engine.addListener(this); // Observer registrieren
    }

    private void setupKeyBindings(Direction direction, Iterable<Integer> keyCodes) {

        var inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        var actionMap = getActionMap();

        var actionKey = "move_" + direction.name();

        var swingAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameEngine != null) {
                    gameEngine.update(direction);
                }
            }
        };

        keyCodes.forEach(keyCode ->
            inputMap.put(KeyStroke.getKeyStroke(keyCode, 0), actionKey)
        );

        actionMap.put(actionKey, swingAction);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.render((Graphics2D) g, state, GameConstants.TILE_SIZE);
    }
}
