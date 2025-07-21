package com.colossalg;

import java.util.ArrayList;
import java.util.List;

public class Scanner {

    public Scanner(ErrorReporter errorReporter, String source, String file) {
        _errorReporter = errorReporter;
        _source = source;
        _file = file;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            _begLexemeIndex = _curLexemeIndex;
            advance();
            final char begChar = peekBeg();
            switch (begChar) {
                case ' ', '\t', '\r' -> { /* Do nothing for whitespace besides newline */ }
                case '\n' -> _line++;
                case '(' -> addToken(TokenType.LFT_PARENTHESIS);
                case ')' -> addToken(TokenType.RGT_PARENTHESIS);
                case '{' -> addToken(TokenType.LFT_BRACE);
                case '}' -> addToken(TokenType.RGT_BRACE);
                case '[' -> addToken(TokenType.LFT_BRACKET);
                case ']' -> addToken(TokenType.RGT_BRACKET);
                case ',' -> addToken(TokenType.COMMA);
                case '.' -> addToken(TokenType.DOT);
                case ';' -> addToken(TokenType.SEMICOLON);
                case '+' -> addToken(TokenType.ADD);
                case '-' -> addToken(TokenType.SUB);
                case '*' -> addToken(TokenType.MUL);
                case '/' -> addToken(TokenType.DIV);
                case '=' -> tryMatchTrailingEqualsAndScanToken(TokenType.EQUAL_EQUAL, TokenType.EQUAL);
                case '!' -> tryMatchTrailingEqualsAndScanToken(TokenType.BANGS_EQUAL, TokenType.BANGS);
                case '<' -> tryMatchTrailingEqualsAndScanToken(TokenType.LESS_THAN_OR_EQUAL, TokenType.LESS_THAN);
                case '>' -> tryMatchTrailingEqualsAndScanToken(TokenType.MORE_THAN_OR_EQUAL, TokenType.MORE_THAN);
                case '#' -> scanComment();
                case '"' -> scanStringLiteral();
                default -> {
                    if (isDigit(begChar)) {
                        scanNumberLiteral();
                    } else if (begChar == '_' || isAlpha(begChar)) {
                        scanKeywordOrIdentifier();
                    } else {
                        reportError(String.format("Unrecognised character beginning token '%c'.", peekBeg()));
                    }
                }
            }
        }
        addToken(TokenType.EOF);
        return _tokens;
    }

    private static boolean isAlpha(char c) {
        return (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z'));
    }

    private static boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }

    private void tryMatchTrailingEqualsAndScanToken(TokenType yesTrailingEquals, TokenType noTrailingEquals) {
        if (matches('=')) {
            advance();
            addToken(yesTrailingEquals);
        } else {
            addToken(noTrailingEquals);
        }
    }

    private void scanComment() {
        while (!isAtEnd() && !matches('\n')) {
            advance();
        }
    }

    private void scanStringLiteral() {
        while (!isAtEnd() && !matches('"')) {
            if (matches('\n')) {
                _line++;
            }
            advance();
        }
        if (isAtEnd()) {
            reportError("String literal not terminated before EOF.");
        } else {
            advance(); // Consume ending '"'
            final var literal = _source.substring(_begLexemeIndex + 1, _curLexemeIndex - 1);
            addToken(TokenType.STRING, literal);
        }
    }

    private void scanNumberLiteral() {
        while (!isAtEnd() && isDigit(peekCur())) {
            advance();
        }
        if (matches('.')) {
            advance();
        }
        while (!isAtEnd() && isDigit(peekCur())) {
            advance();
        }
        final var literal = Double.parseDouble(_source.substring(_begLexemeIndex, _curLexemeIndex));
        addToken(TokenType.NUMBER, literal);
    }

    private void scanKeywordOrIdentifier() {
        while (!isAtEnd() && (peekCur() == '_' || isAlpha(peekCur()) || isDigit(peekCur()))) {
            advance();
        }

        final var literal = _source.substring(_begLexemeIndex, _curLexemeIndex);
        switch (literal)
        {
            // Keywords
            case "and" -> addToken(TokenType.AND);
            case "or" -> addToken(TokenType.OR);
            case "false" -> addToken(TokenType.FALSE);
            case "true" -> addToken(TokenType.TRUE);
            case "nil" -> addToken(TokenType.NIL);
            case "var" -> addToken(TokenType.VAR);
            case "if" -> addToken(TokenType.IF);
            case "else" -> addToken(TokenType.ELSE);
            case "for" -> addToken(TokenType.FOR);
            case "while" -> addToken(TokenType.WHILE);
            case "fun" -> addToken(TokenType.FUN);
            case "return" -> addToken(TokenType.RETURN);
            case "class" -> addToken(TokenType.CLASS);
            case "new" -> addToken(TokenType.NEW);
            case "print" -> addToken(TokenType.PRINT);
            // Identifiers
            default -> addToken(TokenType.IDENTIFIER, literal);
        }
    }

    private void advance() {
        _curLexemeIndex++;
    }

    private boolean matches(char c) {
        return !isAtEnd() && peekCur() == c;
    }

    private void reportError(String what) {
        _errorReporter.report(new JocksError("Scanner", _file, _line, what));
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        _tokens.add(new Token(type, literal, _source.substring(_begLexemeIndex, _curLexemeIndex), _file, _line));
    }

    private boolean isAtEnd() {
        return _curLexemeIndex >= _source.length();
    }

    private char peekBeg() {
        return _source.charAt(_begLexemeIndex);
    }

    private char peekCur() {
        return _source.charAt(_curLexemeIndex);
    }

    private final ErrorReporter _errorReporter;
    private final String _source;
    private final List<Token> _tokens = new ArrayList<>();
    private int _begLexemeIndex = 0;
    private int _curLexemeIndex = 0;
    private final String _file;
    private int _line = 1;
}
