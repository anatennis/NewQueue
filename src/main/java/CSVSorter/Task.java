package CSVSorter;

import java.math.BigInteger;
import java.util.Scanner;

public class Task {
    private static Integer res = -1;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Введите равенство: ");
        String input = sc.nextLine();
        String s = input.replace(" ", "").toLowerCase();
        if (!s.contains("=")) {
            System.out.println(res);
            return;
        }
        int index = s.indexOf("=");
        String firstNum = s.substring(0, index);
        String secNum = s.substring(index + 1);
        boolean isFirstComplex = !firstNum.contains("+") && !firstNum.contains("-");
        boolean isSecComplex = !secNum.contains("+") && !secNum.contains("-");
        NumberWithFound number1 = getNumber(firstNum, isFirstComplex);
        NumberWithFound number2 = getNumber(secNum, isSecComplex);

        int minF = Math.max(number1.f, number2.f);
        for (int i = minF; i < 37; i++) {
            NumberWithFound number1WithFoundation = getNumberWithFoundation(firstNum, isFirstComplex, i);
            NumberWithFound number2WithFoundation = getNumberWithFoundation(secNum, isSecComplex, i);
            if (number1WithFoundation.n.equals(number2WithFoundation.n)) {
                res = i;
                break;
            }
        }

        System.out.println(res);

    }

    public static NumberWithFound getNumber(String s, boolean hasOneNumber) {
        int minf1;
        if (!hasOneNumber) {
            NumberWithFound num = new NumberWithFound("", 0);
            getNumber(s, num, false);
            return num;
        } else {
            minf1 = getFoundation(s);
            return new NumberWithFound(s, minf1);
        }
    }

    public static int getNumber(String s, NumberWithFound num, boolean hasOneNumber) { //2+3+1 = 6
        int minf1;
        if (!hasOneNumber) {
            int beginIndex = getBeginIndex(s);
            String n1 = s.substring(0, beginIndex);
            String n2 = s.substring(beginIndex + 1);
            minf1 = getFoundation(n1);
            int minf2;
            if (n1.equals(n2)) {
                minf2 = minf1;
                num.setN(n2);
                num.setF(minf2);
            } else {
                if (n2.contains("+") || n2.contains("-")) {
                    minf2 = getNumber(n2, num, false);
                } else {
                    minf2 = getNumber(n2, num, true);
                }
            }
            int found = Math.max(minf1, minf2);
            BigInteger result = s.charAt(beginIndex) == '+' ?
                    new BigInteger(n1, found).add(new BigInteger(num.n, num.f)) :
                    new BigInteger(n1, found).subtract(new BigInteger(num.n, num.f));
            num.setF(found);
            num.setN(result.toString(found));
            return found;
        } else {//3498rjfkselvnricmldclanv
            minf1 = getFoundation(s);
            num.setF(minf1);
            num.setN(s);
            return minf1;
        }
    }//2w3+ds89dhsh-13+42=45+hs-36+wywy

    public static NumberWithFound getNumberWithFoundation(String s, boolean hasOneNumber, int found) {
        if (!hasOneNumber) {
            NumberWithFound num = new NumberWithFound("", 0);
            getNumberWithFoundation(s, num, false, found);
            return num;
        } else {
            return new NumberWithFound(s, found);
        }
    }

    public static void getNumberWithFoundation(String s, NumberWithFound num, boolean hasOneNumber, int found) { //2+3+1 = 6
        if (!hasOneNumber) {
            int beginIndex = getBeginIndex(s);
            String n1 = s.substring(0, beginIndex);
            String n2 = s.substring(beginIndex + 1);
            getNumberWithFoundation(n2, num, !n2.contains("+") && !n2.contains("-"), found);
            BigInteger result = s.charAt(beginIndex) == '+' ?
                    new BigInteger(n1, found).add(new BigInteger(num.n, found)) :
                    new BigInteger(n1, found).subtract(new BigInteger(num.n, found));
            num.setF(found);
            num.setN(result.toString(found));
        } else {
            num.setF(found);
            num.setN(s);
        }
    }

    public static int getBeginIndex(String s) {
        int indexPlus = s.indexOf("+");
        int indexMinus = s.indexOf("-");
        if (indexPlus == -1) {
            return indexMinus;
        } else if (indexMinus == -1) {
            return indexPlus;
        } else {
            return Math.min(indexPlus, indexMinus);
        }
    }

    private static int getFoundation(String number) {
        boolean hasLetters = false;
        char[] chars = number.toCharArray();
        char biggest = chars[0];
        for (Character c : chars) {
            if (Character.isLetter(c)) hasLetters = true;
            if (c > biggest) {
                biggest = c;
            }
        }
        return hasLetters ? 10 + (biggest - 97 + 1) : Integer.parseInt(String.valueOf(biggest)) + 1;
    }

    private static class NumberWithFound {
        String n;

        public void setF(int f) {
            this.f = f;
        }

        public void setN(String n) {
            this.n = n;
        }

        int f;

        NumberWithFound(String n, int f) {
            this.f = f;
            this.n = n;
        }

        @Override
        public String toString() {
            return "Number " + n + " with foundation " + f;
        }
    }
}
