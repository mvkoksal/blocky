package com.gamewerks.blocky.engine;

import com.gamewerks.blocky.util.Constants;
import com.gamewerks.blocky.util.Position;
import java.util.Random;

public class BlockyGame {
    private static final int LOCK_DELAY_LIMIT = 30;
    
    private Board board;
    private Piece activePiece;
    private Direction movement;
    
    private int lockCounter;

    private int flag=0;
    private PieceKind array[] = PieceKind.values();
    
    public BlockyGame() {
        board = new Board();
        movement = Direction.NONE;
        lockCounter = 0;
        trySpawnBlock();
    }

    private void Shuffle (PieceKind array[]) {        
        for (int i = array.length -1; i > 0; i--) {
            int randomIndex = (int)(Math.random() * (i+1));
            PieceKind temp = array[i];
            array[i] = array[randomIndex];
            array[randomIndex] = temp;
        }
    }

    private void trySpawnBlock() {
        
        if (activePiece == null) {
        if (flag == 0) {
            Shuffle(array);
        }
        if (flag < array.length) {
            activePiece = new Piece(array[flag], new Position(3, Constants.BOARD_WIDTH / 2 - 2));
            flag++;
        }
        if (flag == array.length) {
            flag = 0;
        }
        if (board.collides(activePiece)) {
            System.exit(0);
        }
    }
    }
    
    private void processMovement() {
        Position nextPos;
        switch(movement) {
        case NONE:
            nextPos = activePiece.getPosition();
            System.out.println("processmovement");
            break;
        case LEFT:
            nextPos = activePiece.getPosition().add(0, -1);
            System.out.println("processmovement");
            break;
        case RIGHT:
            nextPos = activePiece.getPosition().add(0, 1);
            System.out.println("processmovement");
            break;
        default:
            throw new IllegalStateException("Unrecognized direction: " + movement.name());
        }
        if (!board.collideshelper(activePiece.getLayout(), nextPos)) {
            activePiece.moveTo(nextPos);
        }
    }
    
    private void processGravity() {
        System.out.println("processgravity");
        Position nextPos = activePiece.getPosition().add(1, 0);
        if (!board.collideshelper(activePiece.getLayout(), nextPos)) {
            lockCounter = 0;
            activePiece.moveTo(nextPos);
        } else {
            if (lockCounter < LOCK_DELAY_LIMIT) {
                lockCounter += 1;
            } else {
                board.addToWell(activePiece);
                lockCounter = 0;
                activePiece = null;
            }
        }
    }
    
    private void processClearedLines() {
        board.deleteRows(board.getCompletedRows());
    }
    
    public void step() {
        trySpawnBlock();
        processMovement();
        processGravity();
        processClearedLines();
    }
    
    public boolean[][] getWell() {
        return board.getWell();
    }
    
    public Piece getActivePiece() { return activePiece; }
    public void setDirection(Direction movement) { this.movement = movement; }
    public void rotatePiece(boolean dir) {    
        Position originalPos = activePiece.getPosition();
        activePiece.rotate(dir);
        if (!board.collideshelper(activePiece.getLayout(), originalPos)) { return; }
        activePiece.rotate(!dir);
    }
}
