package com.anjalipancholi.assignment2;

import edu.princeton.cs.algs4.Picture;

import java.util.Arrays;

public class SeamCarver {

    private static final double BORDER_ENERGY = 1000d;
    private static final boolean VERTICAL = true, HORIZONTAL = false;
    private Picture pic;

    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("No picture");
        }
        this.pic = new Picture(picture);
    }

    public Picture picture() {
        return new Picture(this.pic);
    }

    public int width() {
        return pic.width();
    }

    public int height() {
        return pic.height();
    }


    public double energy(int col, int row) {
        validatePixel(col, row);

        if (col == 0 || col == width() - 1 || row == 0 || row == height() - 1) {
            return BORDER_ENERGY;
        }

        double redX = pic.get(col + 1, row).getRed() - pic.get(col - 1, row).getRed();
        double greenX = pic.get(col + 1, row).getGreen() - pic.get(col - 1, row).getGreen();
        double blueX = pic.get(col + 1, row).getBlue() - pic.get(col - 1, row).getBlue();
        double redY = pic.get(col, row + 1).getRed() - pic.get(col, row - 1).getRed();
        double greenY = pic.get(col, row + 1).getGreen() - pic.get(col, row - 1).getGreen();
        double blueY = pic.get(col, row + 1).getBlue() - pic.get(col, row - 1).getBlue();

        return Math.sqrt(redX * redX + greenX * greenX + blueX * blueX + redY * redY + greenY * greenY + blueY * blueY);
    }

    public int[] findHorizontalSeam() {
        Grid[][] energy = new Grid[height()][width()];
        for (int i = 0; i < height(); i++) {
            energy[i][0] = new Grid(BORDER_ENERGY, -1);
        }
        for (int col = 1; col < width(); col++) {
            energy[0][col] = new Grid(BORDER_ENERGY, -1);
            for (int row = 0; row < height(); row++) {
                helperHorizontal(energy, row, col);
            }
        }
        return extractHorizontalSeam(energy);
    }

    public int[] findVerticalSeam() {
        Grid[][] energy = new Grid[height()][width()];
        for (int i = 0; i < width(); i++) {
            energy[0][i] = new Grid(BORDER_ENERGY, -1);
        }

        for (int row = 1; row < height(); row++) {
            energy[row][0] = new Grid(BORDER_ENERGY, -1);
            for (int col = 0; col < width(); col++) {
                helperVertical(energy, row, col);
            }
        }
        return extractVerticalSeam(energy);
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (!isValidSeam(seam, HORIZONTAL)) {
            throw new IllegalArgumentException("Illegal seam");
        }
        Picture seamedPicture = new Picture(width(), height() - 1);

        for (int col = 0; col < width(); col++) {
            int rowBias = 0;
            for (int row = 0; row < height() - 1; row++) {
                if (seam[col] == row) {
                    rowBias = 1;
                }
                seamedPicture.set(col, row, pic.get(col, row + rowBias));
            }
        }
        this.pic = seamedPicture;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (!isValidSeam(seam, VERTICAL)) {
            throw new IllegalArgumentException("Illegal seam");
        }
        Picture seamedPicture = new Picture(width() - 1, height());
        for (int row = 0; row < height(); row++) {
            int colBias = 0;
            for (int col = 0; col < width() - 1; col++) {
                if (seam[row] == col) {
                    colBias = 1;
                }
                seamedPicture.set(col, row, pic.get(col + colBias, row));
            }
        }
        this.pic = seamedPicture;
    }

    private void validatePixel(int col, int row) {
        if (!isValidPixel(col, row)) {
            throw new IllegalArgumentException("Invalid pixel: col: " + col + ", row: " + row);
        }
    }

    private boolean isValidPixel(int col, int row) {
        return col > -1 && col < width() && row > -1 && row < height();
    }

    private void helperVertical(Grid[][] energies, int row, int col) {
        double energy = energy(col, row);
        Grid[] paths = {
                new Grid(isValidPixel(col - 1, row - 1) ? energy + energies[row - 1][col - 1].energy : Double.MAX_VALUE, col - 1),
                new Grid(isValidPixel(col, row - 1) ? energy + energies[row - 1][col].energy : Double.MAX_VALUE, col),
                new Grid(isValidPixel(col + 1, row - 1) ? energy + energies[row - 1][col + 1].energy : Double.MAX_VALUE, col + 1)
        };
        Arrays.sort(paths);
        energies[row][col] = paths[0];
    }
    
    private void helperHorizontal(Grid[][] energies, int row, int col) {
        double myEnergy = energy(col, row);
        Grid[] paths = {
                new Grid(isValidPixel(col - 1, row - 1) ? myEnergy + energies[row - 1][col - 1].energy : Double.MAX_VALUE, row - 1),
                new Grid(isValidPixel(col - 1, row) ? myEnergy + energies[row][col - 1].energy : Double.MAX_VALUE, row),
                new Grid(isValidPixel(col - 1, row + 1) ? myEnergy + energies[row + 1][col - 1].energy : Double.MAX_VALUE, row + 1)
        };
        Arrays.sort(paths);
        energies[row][col] = paths[0];
    }

    private int[] extractVerticalSeam(Grid[][] energies) {
        int[] seam = new int[height()];
        double lowestEnergy = Double.MAX_VALUE;
        int index = -1;
        // find lowest energy
        for (int col = 0; col < width(); col++) {
            if (energies[height() - 1][col].energy < lowestEnergy) {
                lowestEnergy = energies[height() - 1][col].energy;
                index = col;
            }
        }

        int row = height() - 1;
        while (row > -1) {
            seam[row] = index;
            index = energies[row][index].prev;
            row--;
        }
        return seam;
    }

    private int[] extractHorizontalSeam(Grid[][] energies) {
        int[] seam = new int[width()];
        double lowestEnergy = Double.MAX_VALUE;
        int index = -1;
        // find lowest energy
        for (int row = 0; row < height(); row++) {
            if (energies[row][width() - 1].energy < lowestEnergy) {
                lowestEnergy = energies[row][width() - 1].energy;
                index = row;
            }
        }

        int col = width() - 1;
        while (col > -1) {
            seam[col] = index;
            index = energies[index][col].prev;
            col--;
        }
        return seam;
    }

    private boolean isValidSeam(int[] seam, boolean vertical) {

        if (seam == null) {
            return false;
        }

        if ((vertical && seam.length != height()) || (!vertical && seam.length != width())) {
            return false;
        }

        for (int i : seam) {
            if ((i < 0) || (vertical && i >= width()) || (!vertical && i >= height())) {
                return false;
            }
        }
        for (int i = 0; i < seam.length - 1; i++) {
            if (Math.abs(seam[i] - seam[i + 1]) > 1) {
                return false;
            }
        }
        return true;
    }

    private static class Grid implements Comparable<Grid> {
        public final double energy;
        public final int prev;

        public Grid(double energy, int prev) {
            this.energy = energy;
            this.prev = prev;
        }

        @Override
        public int compareTo(Grid o) {
            if (this.energy > o.energy) {
                return 1;
            } else if (this.energy < o.energy) {
                return -1;
            }
            return 0;
        }
    }
}

