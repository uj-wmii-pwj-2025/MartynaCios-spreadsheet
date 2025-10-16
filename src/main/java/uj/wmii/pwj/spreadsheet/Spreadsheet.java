package uj.wmii.pwj.spreadsheet;

public class Spreadsheet {
    public String[][] calculate( String[][] input ) {
        if ( input == null || input.length == 0 ) {
            return input;
        }

        int rows = input.length;
        int cols = input[0].length;

        for ( int i = 0; i < rows; i++ ) {
            for ( int j = 0; j < cols; j++ ) {
                input[i][j] = evaluateCell(input[i][j], input);
            }
        }

        return input;
    }
    private String evaluateCell( String cellValue, String[][] sheet ) {

        if ( !cellValue.startsWith("$") && !cellValue.startsWith("=") ) {
            return cellValue;
        }

        if ( cellValue.startsWith("$") ) {
            int [] coords = parseReference(cellValue);

            int row = coords[0];
            int col = coords[1];
            String refValue = sheet[row][col];
            if ( refValue.startsWith("$") || refValue.startsWith("=") ) {
                return evaluateCell( refValue, sheet );
            }
            return refValue;
        }

        if ( cellValue.startsWith("=") ) {
            return evaluateFormula( cellValue, sheet );
        }

        return cellValue;
    }

    private int [] parseReference( String ref ) {
        String refPart = ref.substring(1);
        int colEnd = 0;

        while ( colEnd < refPart.length() && Character.isLetter(refPart.charAt(colEnd)) ) {
            colEnd++;
        }

        String colStr = refPart.substring( 0, colEnd );
        String rowStr = refPart.substring( colEnd );

        int col = 0;
        for (char c : colStr.toCharArray()) {
            col = col * 26 + (Character.toUpperCase(c) - 'A' + 1) - 1;
        }

        int row = Integer.parseInt(rowStr) - 1;
        return new int [] { row, col };
    }

    private String evaluateFormula( String formula, String[][] sheet ) {
            int openParenthesis = formula.indexOf('(');
            int closePar = formula.indexOf(')', openParenthesis);

            String operation = formula.substring(1, openParenthesis);
            String strParams = formula.substring(openParenthesis + 1, closePar);

            String [] params = splitParams(strParams);

            int val1 = parseParameterValue(params[0], sheet);
            int val2 = parseParameterValue(params[1], sheet);

            int result = 0;
        if ( operation.equals("ADD") ) {
            result = val1 + val2;
        }
        else if ( operation.equals("SUB") ) {
            result = val1 - val2;
        }
        else if ( operation.equals("MUL") ) {
            result = val1 * val2;
        }
        else if ( operation.equals("DIV") ) {
            if ( val2 == 0 ) {
                return "0";
            }
            result = val1 / val2;
        }
        else {
            if ( val2 == 0 ) {
                return "0";
            }
            result = val1 % val2;
        }

        return String.valueOf(result);
    }

    private String [] splitParams( String strParam ) {
        int commaIndice = strParam.indexOf(',');

        String p1 = strParam.substring(0, commaIndice);
        String p2 = strParam.substring(commaIndice + 1);

        return new String [] { p1, p2 };
    }

    private int parseParameterValue( String param, String[][] sheet ) {
        if ( param.startsWith("$") ) {
            int [] coords = parseReference(param);
            int row = coords[0];
            int col = coords[1];
            String refValue = sheet[row][col];

            if ( refValue.startsWith("$") || refValue.startsWith("=") ) {
                String evaluated = evaluateCell(refValue, sheet);
                return Integer.parseInt(evaluated);
            }
            return Integer.parseInt(refValue);

        } else {
            return Integer.parseInt(param);
        }
    }
}
