import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;

/**
gramatica:
<G> ::= <DECLARACAO_ALGORITMO>
<DECLARACAO_ALGORITMO> ::= <INICIO_ALGORITMO> <BLOCO_VARIAVEIS> <BLOCOS>
<INICIO_ALGORITMO> ::= 'ALGORITMO' <ID> ';'
<BLOCO_VARIAVEIS> ::= 'INICIO_VARIAVEIS' <VARIAVEIS> 'FIM_VARIAVEIS'
<VARIAVEIS> ::= <VARIAVEL> ‘,’ <VARIAVEIS>
<VARIAVEIS> ::= <VARIAVEL>
<VARIAVEL> ::= <ID> ':' <TIPO_PRIMITIVO>
<TIPO_PRIMITIVO> ::= 'INTEIRO'
<TIPO_PRIMITIVO> ::= 'DECIMAL'
<BLOCOS> ::= 'INICIO' <TIPO_BLOCOS> 'FIM'
<TIPO_BLOCOS> ::= <TIPO_BLOCO> ‘;’ <TIPO_BLOCOS>
<TIPO_BLOCOS> ::= <TIPO_BLOCO>
<TIPO_BLOCO> ::= <BLOCO_ATRIBUICAO>
<TIPO_BLOCO> ::= <BLOCO_SE>
<TIPO_BLOCO> ::= <BLOCO_ENQUANTO>
<TIPO_BLOCO> ==: <BLOCO_LEIA>
<TIPO_BLOCO> ==: <BLOCO_ESCREVA>
<TIPO_BLOCO> ::= <BLOCO_CHAMAR_FUNCAO>
<BLOCO_ATRIBUICAO> ::= <VAR> '=' <E>
<VAR> ::= ID
<BLOCO_SE> ::= 'SE' <CONDICAO> 'ENTAO' <TIPO_BLOCOS> 'SENAO' <TIPO_BLOCOS> 'FIMSE'
<BLOCO_SE> ::= 'SE' <CONDICAO> 'ENTAO' <TIPO_BLOCOS> 'FIMSE'
<BLOCO_ENQUANTO> ::= 'ENQUANTO' <CONDICAO> 'FACA' <TIPO_BLOCOS> 'FIM_ENQUANTO'
<BLOCO_LEIA> ::= 'LEIA' '(' <VAR> ')'
<BLOCO_ESCREVA> ::= 'ESCREVA' '(' <E> ') '
<BLOCO_CHAMAR_FUNCAO> ::= 'CHAMAR' <ID> '(' <ARGUMENTOS> ')'
<ARGUMENTOS> ::= <E>
<ARGUMENTOS> ::= <E> ',' <E>
<CONDICAO> ::= <E> '==' <E>
<CONDICAO> ::= <E> '!=' <E>
<CONDICAO> ::= <E> '>' <E>
<CONDICAO> ::= <E> '>=' <E>
<CONDICAO> ::= <E> '<' <E>
<CONDICAO> ::= <E> '<=' <E>
<E> ::= <E> + <T>
<E> ::= <E> - <T>
<E> ::= <T>
<T> ::= <T> * <F>
<T> ::= <T> / <F>
<T> ::= <T> % <F>
<T> ::= <T> @ <F>
<T> ::= <T> $ <F>
<T> ::= <F>
<F> ::= -<X>
<F> ::= <X> ** <F>
<F> ::= <X>
<X> ::= '(' <E> ')'
<X> ::= [0-9]+('.'[0-9]+)
<X> ::= <ID>
<ID> ::= [A-Z]+([A-Z]_[0-9]*)
 */

public class Compilador {
	// Lista de tokens
	static final int T_ALGORITMO 			= 1;
	static final int T_PONTO_VIRGULA 		= 2;
	static final int T_INICIO_VARIAVEIS 	= 3;
	static final int T_FIM_VARIAVEIS 		= 4;
	static final int T_DOIS_PONTOS 			= 5;
	static final int T_INTEIRO 				= 6;
	static final int T_DECIMAL 				= 7;
	static final int T_INICIO 				= 8;
	static final int T_FIM 					= 9;
	static final int T_RECEBER 				= 10;
	static final int T_ABRE_COLCHETES 		= 11;
	static final int T_FECHA_COLCHETES 		= 12;
	static final int T_SE 					= 13;
	static final int T_ENTAO 				= 14;
	static final int T_SE_NAO 				= 15;
	static final int T_FIM_SE 				= 16;
	static final int T_ENQUANTO 			= 17;
	static final int T_FACA 				= 18;
	static final int T_FIM_ENQUANTO 		= 19;
	static final int T_LEIA 				= 20;
	static final int T_ABRE_PARENTESES 		= 21;
	static final int T_FECHA_PARENTESES 	= 22;
	static final int T_ESCREVA 				= 23;
	static final int T_VIRGULA 				= 24;
	static final int T_IGUAL 				= 25;
	static final int T_DIFERENTE 			= 26;
	static final int T_MAIOR 				= 27;
	static final int T_MAIOR_IGUAL 			= 28;
	static final int T_MENOR 				= 29;
	static final int T_MENOR_IGUAL 			= 30;
	static final int T_MAIS 				= 31;
	static final int T_MENOS 				= 32;
	static final int T_VEZES 				= 33;
	static final int T_DIVIDIDO 			= 34;
	static final int T_RESTO 				= 35;
	static final int T_ELEVADO 				= 36;
	static final int T_NAO 					= 37;
	static final int T_ASPAS_SIMPLES 		= 38;
	static final int T_ID 					= 39;
	static final int T_NUMERO 				= 40;
	static final int T_SOMA_DOBRA			= 41;
	static final int T_DIVIDE_DOBRA 		= 42;
	
	static final int T_FIM_FONTE 			= 90;
	static final int T_ERRO_LEX 			= 98;
	static final int T_NULO 				= 99;

	static final int FIM_ARQUIVO 			= 226;

	static final int E_SEM_ERROS 			= 0;
	static final int E_ERRO_LEXICO 			= 1;
	static final int E_ERRO_SINTATICO 		= 2;
	static final int E_ERRO_SEMANTICO       = 3;

	static File arqFonte;
	static BufferedReader rdFonte;
	static File arqDestino;

	static char lookAhead;
	static int token;
	static String lexema;
	static int ponteiro;
	static String linhaFonte;
	static int linhaAtual;
	static int colunaAtual;
	static String mensagemDeErro;
	static StringBuffer tokensIdentificados = new StringBuffer();

	static StringBuffer regrasReconhecidas = new StringBuffer();
	static int estadoCompilacao;
	
	static String ultimoLexema;
	static StringBuffer codigoPython = new StringBuffer();
	static int nivelIdentacao = 0;
	static String exp_0;
	static String exp_1;
	static String exp_2;
	static String exp_alvo;
	static NodoPilhaSemantica nodo;
	static NodoPilhaSemantica nodo_0;
	static NodoPilhaSemantica nodo_1;
	static NodoPilhaSemantica nodo_2;
	static PilhaSemantica pilhaSemantica = new PilhaSemantica();
	static HashMap<String,Integer> tabelaSimbolos = new HashMap<String,Integer>();

	public static void main( String s[] ) throws ErroLexicoException
	{
		try {
			abreArquivo();
			abreDestino();
			linhaAtual = 0;
			colunaAtual = 0;
			ponteiro = 0;
			linhaFonte = "";
			token = T_NULO;
			mensagemDeErro = "";
			tokensIdentificados.append("Tokens reconhecidos: \n\n");
			regrasReconhecidas.append("\n\nRegras reconhecidas: \n\n");
			estadoCompilacao = E_SEM_ERROS;

			// posiciono no primeiro token
			movelookAhead();
			buscaProximoToken();

			analiseSintatica();

			exibeSaida();

			gravaSaida(arqDestino);

			fechaFonte();

		} catch( FileNotFoundException fnfe ) {
			JOptionPane.showMessageDialog( null, "Arquivo nao existe!", "FileNotFoundException!", JOptionPane.ERROR_MESSAGE );
		} catch( UnsupportedEncodingException uee ) {
			JOptionPane.showMessageDialog( null, "Erro desconhecido", "UnsupportedEncodingException!", JOptionPane.ERROR_MESSAGE );
		} catch( IOException ioe ) {
			JOptionPane.showMessageDialog( null, "Erro de io: " + ioe.getMessage(), "IOException!", JOptionPane.ERROR_MESSAGE );
		} catch( ErroLexicoException ele ) {
			JOptionPane.showMessageDialog( null, ele.getMessage(), "Erro Lexico Exception!", JOptionPane.ERROR_MESSAGE );
		} catch( ErroSintaticoException ese ) {
			JOptionPane.showMessageDialog( null, ese.getMessage(), "Erro Sintatico Exception!", JOptionPane.ERROR_MESSAGE );
		} catch( ErroSemanticoException esme ) {
			JOptionPane.showMessageDialog( null, esme.getMessage(), "Erro Semantico Exception!", JOptionPane.ERROR_MESSAGE );          
		} finally {
			System.out.println( "Execucao terminada!" );
		}
	}

	static void analiseSintatica() throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
		g();

		if ( estadoCompilacao == E_ERRO_LEXICO ) {
			JOptionPane.showMessageDialog( null, mensagemDeErro, "Erro Lexico!", JOptionPane.ERROR_MESSAGE );
		} else if ( estadoCompilacao == E_ERRO_SINTATICO ) {
			JOptionPane.showMessageDialog( null, mensagemDeErro, "Erro Sintatico!", JOptionPane.ERROR_MESSAGE );
		} else if ( estadoCompilacao == E_ERRO_SEMANTICO ) {
			JOptionPane.showMessageDialog( null, mensagemDeErro, "Erro Sintatico!", JOptionPane.ERROR_MESSAGE );
		} else {
			JOptionPane.showMessageDialog( null, "Analise Sintatica terminada sem erros", "Analise Sintatica terminada!", JOptionPane.INFORMATION_MESSAGE );
			acumulaRegraSintaticaReconhecida( "<G>" );
		}
	}

	// <G> ::= <DECLARACAO_ALGORITMO>
	private static void g() throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
		declaracaoAlgoritmo();
		buscaProximoToken();
		acumulaRegraSintaticaReconhecida("<G> ::= <DECLARACAO_ALGORITMO>");
	}

	// <DECLARACAO_ALGORITMO> ::= <INICIO_ALGORITMO> <BLOCO_VARIAVEIS> <BLOCOS>
	private static void declaracaoAlgoritmo() throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
		inicioAlgoritmo();
		blocoVariaveis();
		blocos();
		regraSemantica( 1 );
		acumulaRegraSintaticaReconhecida("<DECLARACAO_ALGORITMO> ::= <INICIO_ALGORITMO> <BLOCO_VARIAVEIS> <BLOCOS>");
	}

	// <INICIO_ALGORITMO> ::= 'ALGORITMO' <ID> ';'
	private static void inicioAlgoritmo() throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
		if (token == T_ALGORITMO) {
			buscaProximoToken();
			regraSemantica( 0 ); 
			id();
			if (token == T_PONTO_VIRGULA) {
				buscaProximoToken();
				acumulaRegraSintaticaReconhecida("<INICIO_ALGORITMO> ::= 'ALGORITMO' <ID> ';'");
			} else {
				registraErroSintatico("Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n';' esperado, mas encontrei: " + lexema);
			}
		} else {
			registraErroSintatico("Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n'ALGORITMO' esperado, mas encontrei: " + lexema);
		}
	}

	// <BLOCO_VARIAVEIS> ::= 'INICIO_VARIAVEIS' <VARIAVEIS> 'FIM_VARIAVEIS'
	private static void blocoVariaveis() throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
		if (token == T_INICIO_VARIAVEIS) {
			buscaProximoToken();
			variaveis();
			if (token == T_FIM_VARIAVEIS) {
				buscaProximoToken();
				acumulaRegraSintaticaReconhecida("<BLOCO_VARIAVEIS> ::= 'INICIO_VARIAVEIS' <VARIAVEIS> 'FIM_VARIAVEIS'");
			} else {
				registraErroSintatico("Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n'FIM_VARIAVEIS' esperado, mas encontrei: " + lexema);
			}
		}
	}

	// <VARIAVEIS> ::= <VARIAVEL> ',' <VARIAVEIS> | <VARIAVEL>
	private static void variaveis() throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
		variavel();
		while ((token == T_VIRGULA)) {
			buscaProximoToken();
			variavel();
		}
		acumulaRegraSintaticaReconhecida("<VARIAVEIS> ::= <VARIAVEL> ',' <VARIAVEIS> | <VARIAVEL>");
	}

	// <VARIAVEL> ::= <ID> ':' <TIPO_PRIMITIVO>
	private static void variavel() throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
		id();
		regraSemantica( 2 );
		regraSemantica( 12 );
		regraSemantica( 26 );
		if (token == T_DOIS_PONTOS) {
			buscaProximoToken();
			tipoPrimitivo();
			acumulaRegraSintaticaReconhecida("<VARIAVEL> ::= <ID> ':' <TIPO_PRIMITIVO>");
		} else {
			registraErroSintatico("Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n':' esperado, mas encontrei: " + lexema);
		}
	}
	
	// <VAR> ::= ID
	private static void var() throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
		e();
		regraSemantica(4);
		acumulaRegraSintaticaReconhecida("<VAR> ::= ID");
	}

	// <TIPO_PRIMITIVO> ::= 'INTEIRO' | 'DECIMAL'
	private static void tipoPrimitivo() throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
		switch (token) {
		case T_INTEIRO:
			buscaProximoToken();
			break;
		case T_DECIMAL:
			buscaProximoToken();
			break;
		default:
			registraErroSintatico("Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\nEsperava um tipo primitivo. Encontrei: " + lexema);
		}
		acumulaRegraSintaticaReconhecida("<TIPO_PRIMITIVO> ::= 'INTEIRO' | 'DECIMAL'");
	}

	// <BLOCOS> ::= 'INICIO' <TIPO_BLOCOS> 'FIM'
	private static void blocos() throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
		if (token == T_INICIO) {
			buscaProximoToken();
			tipoBlocos();
			if (token == T_FIM) {
				buscaProximoToken();
				acumulaRegraSintaticaReconhecida("<BLOCOS> ::= 'INICIO' <TIPO_BLOCO> 'FIM'");
			} else {
				registraErroSintatico("Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n'FIM' esperado, mas encontrei: " + lexema);
			}
		} else {
			registraErroSintatico("Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n'INICIO' esperado, mas encontrei: " + lexema);
		}
	}

	// <TIPO_BLOCOS> ::= <TIPO_BLOCO> ';' <TIPO_BLOCOS> | <TIPO_BLOCO>
	private static void tipoBlocos() throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
		tipoBloco();
		while ((token == T_PONTO_VIRGULA)) {
			buscaProximoToken();
			tipoBloco();
		}
		acumulaRegraSintaticaReconhecida("<TIPO_BLOCOS> ::= <TIPO_BLOCO> ';' <TIPO_BLOCOS> | <TIPO_BLOCO>");
	}

	// <TIPO_BLOCO> ::= <BLOCO_ATRIBUICAO> | <BLOCO_SE> | <BLOCO_ENQUANTO> | <BLOCO_LEIA> | <BLOCO_ESCREVA>
	private static void tipoBloco() throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
		switch (token) {
			case T_SE: blocoSe(); break;
			case T_ENQUANTO: blocoEnquanto(); break;
			case T_LEIA: blocoLeia(); break;
			case T_ESCREVA: blocoEscreva(); break;
			case T_ID: blocoAtribuicao(); break;
		default:
			registraErroSintatico("Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\nComando nao identificado, mas encontrei: " + lexema);
		}
		acumulaRegraSintaticaReconhecida("<TIPO_BLOCO> ::= <BLOCO_ATRIBUICAO>|<BLOCO_SE>|<BLOCO_ENQUANTO>|<BLOCO_LEIA>|<BLOCO_ESCREVA>|<BLOCO_CHAMAR_FUNCAO>");
	}

	// <BLOCO_ATRIBUICAO> ::= <VAR> '=' <E>
	private static void blocoAtribuicao() throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
		var();
		if (token == T_RECEBER) {
			buscaProximoToken();
			e();
			regraSemantica( 3 );
			acumulaRegraSintaticaReconhecida("<BLOCO_ATRIBUICAO> ::= <VAR> '=' <E>");
		} else {
			registraErroSintatico("Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n'=' esperado, mas encontrei: " + lexema);
		}
	}

	// <BLOCO_SE> ::= 'SE' <CONDICAO> 'ENTAO' <TIPO_BLOCOS> 'SENAO' <TIPO_BLOCOS> 'FIMSE' | 'SE' <CONDICAO> 'ENTAO' <TIPO_BLOCOS> 'FIMSE'
	private static void blocoSe() throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
		if (token == T_SE) {
			buscaProximoToken();
			condicao();
			regraSemantica( 17 );
			if (token == T_ENTAO) {
				buscaProximoToken();
				tipoBlocos();
				regraSemantica( 16 );
				if (token == T_SE_NAO) {
					buscaProximoToken();
					regraSemantica( 18 );
					tipoBlocos();
					regraSemantica( 16 );
				}
				if (token == T_FIM_SE) {
					buscaProximoToken();
					acumulaRegraSintaticaReconhecida("<BLOCO_SE> ::= 'SE' <E> 'ENTAO' <TIPO_BLOCOS> 'SENAO' <TIPO_BLOCOS> 'FIMSE' | 'SE' <E> 'ENTAO' <TIPO_BLOCOS> 'FIMSE'");
				} else {
					registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'FIM_SE' esperado mas encontrei: " + lexema);
				}
			} else {
				registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'ENTAO' esperado mas encontrei: " + lexema);
			}
		} else {
			registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'SE' esperado mas encontrei: " + lexema);
		}
	}

	// <BLOCO_ENQUANTO> ::= 'ENQUANTO' <CONDICAO> 'FACA' <TIPO_BLOCOS> 'FIM_ENQUANTO'
	private static void blocoEnquanto() throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
		if (token == T_ENQUANTO) {
			buscaProximoToken();
			condicao();
			regraSemantica( 15 );
			if (token == T_FACA) {
				buscaProximoToken();
				tipoBlocos();
				regraSemantica( 16 );
				if (token == T_FIM_ENQUANTO) {
					buscaProximoToken();
					acumulaRegraSintaticaReconhecida("<BLOCO_ENQUANTO> ::= 'ENQUANTO' <E> 'FACA' <TIPO_BLOCOS> 'FIM_ENQUANTO'");
				} else {
					registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'FIM_ENQUANTO' esperado mas encontrei: " + lexema);
				}
			} else {
				registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'FACA' esperado mas encontrei: " + lexema);
			}
		} else {
			registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'ENQUANTO' esperado mas encontrei: " + lexema);
		}
	}

	// <BLOCO_LEIA> ::= 'LEIA' '(' <VAR> ')'
	private static void blocoLeia() throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
		if (token == T_LEIA) {
			buscaProximoToken();
			if (token == T_ABRE_PARENTESES) {
				buscaProximoToken();
				var();
				if (token == T_FECHA_PARENTESES) {
					buscaProximoToken();
					regraSemantica( 14 );
					acumulaRegraSintaticaReconhecida("<BLOCO_LEIA> ::= 'LEIA' '(' <VAR> ')'");
				} else {
					registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n')' esperado mas encontrei: " + lexema);
				}
			} else {
				registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'(' esperado mas encontrei: " + lexema);
			}
		} else {
			registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'LEIA' esperado mas encontrei: " + lexema);
		}
	}

	// <BLOCO_ESCREVA> ::= 'ESCREVA' '(' <E> ')'
	private static void blocoEscreva() throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
		if (token == T_ESCREVA) {
			buscaProximoToken();
			if (token == T_ABRE_PARENTESES) {
				buscaProximoToken();
				e();
				if (token == T_FECHA_PARENTESES) {
					buscaProximoToken();
					regraSemantica( 25 );
					acumulaRegraSintaticaReconhecida("<BLOCO_ESCREVA> ::= 'ESCREVA' '(' <E> ')'");
				} else {
					registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n')' esperado mas encontrei: " + lexema);
				}
			} else {
				registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'(' esperado mas encontrei: " + lexema);
			}
		} else {
			registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n'ESCREVA' esperado mas encontrei: " + lexema);
		}
	}
	
	// <CONDICAO> ::= <E> '==' <E>
	// <CONDICAO> ::= <E> '!=' <E>
	// <CONDICAO> ::= <E> '>' <E>
	// <CONDICAO> ::= <E> '>=' <E>
	// <CONDICAO> ::= <E> '<' <E>
	// <CONDICAO> ::= <E> '<=' <E>
	private static void condicao() throws ErroLexicoException, IOException, ErroSintaticoException, ErroSemanticoException {
		e();
		switch (token) {
			case T_IGUAL: buscaProximoToken(); e(); regraSemantica( 23 ); break;
			case T_DIFERENTE: buscaProximoToken(); e(); regraSemantica( 24 ); break;
			case T_MAIOR: buscaProximoToken(); e(); regraSemantica( 19 ); break;
			case T_MENOR_IGUAL: buscaProximoToken(); e(); regraSemantica( 22 ); break;
			case T_MENOR: buscaProximoToken(); e(); regraSemantica( 20 ); break;
			case T_MAIOR_IGUAL: buscaProximoToken(); e(); regraSemantica( 21 ); break;
		default:
			registraErroSintatico("Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\nEsperava um operador logico. Encontrei: " + lexema);
		}
		acumulaRegraSintaticaReconhecida("<CONDICAO> ::= <E> ('OU'|'E'|'=='|'!='|'>'|'>='|'<'|'<=') <E>");
	}

	// <E> ::= <E> + <T>
	// <E> ::= <E> - <T>
	// <E> ::= <T>
	private static void e() throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
		t();
		while ( (token == T_MAIS) || (token == T_MENOS) ) {
			switch( token ) {
				case T_MAIS: { 
		      		buscaProximoToken();
		      		t();
		      		regraSemantica( 5 );
		      	}
		      	break;
		      	case T_MENOS: { 
		      		buscaProximoToken();
		      		t();
		      		regraSemantica( 6 );
		      	}
		      	break;
			}
		}
		acumulaRegraSintaticaReconhecida("<E> ::= <E> + <T>|<E> - <T>|<T> ");
	}

	// <T> ::= <T> * <F>
	// <T> ::= <T> / <F>
	// <T> ::= <T> % <F>
	// <T> ::= <T> @ <F>
	// <T> ::= <T> $ <F>
	// <T> ::= <F>
	private static void t() throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
		f();
		while ( (token == T_VEZES) || (token == T_DIVIDIDO) || (token == T_RESTO) || (token == T_SOMA_DOBRA) || (token == T_DIVIDE_DOBRA) ) {
			switch ( token ) {
				case T_VEZES: { 
					buscaProximoToken();
					f();
					regraSemantica( 7 );
				}
				break;
				case T_DIVIDIDO: { 
					buscaProximoToken();
					f();
					regraSemantica( 8 );
				}
				break;
				case T_RESTO: { 
					buscaProximoToken();
					f();
					regraSemantica( 9 );
				}
				break;
				case T_SOMA_DOBRA: { 
					buscaProximoToken();
                	f();
                	regraSemantica( 27 );
				}
				break;
				case T_DIVIDE_DOBRA: { 
					buscaProximoToken();
                	f();
                	regraSemantica( 28 );
				}
				break;
			}
		}
		acumulaRegraSintaticaReconhecida("<T> ::= <T> * <F>|<T> / <F>|<T> % <F>|<T> @ <F>|<T> $ <F>|<F>");
	}

	// <F> ::= -<F>
	// <F> ::= <X> ** <F>
	// <F> ::= <X>
	private static void f() throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
		if (token == T_MENOS) {
			buscaProximoToken();
			f();
		} else {
			x();
			while (token == T_ELEVADO) {
				buscaProximoToken();
				x();
				regraSemantica( 10 );
			}
		}
		acumulaRegraSintaticaReconhecida("<F> ::= -<F>|<X> ** <F>|<X> ");

	}

	// <X> ::= '(' <E> ')'
	// <X> ::= [0-9]+('.'[0-9]+)
	// <X> ::= <ID>
	private static void x() throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
		switch (token) {
		case T_ID:
			buscaProximoToken();
			acumulaRegraSintaticaReconhecida("<X> ::= <ID>");
			regraSemantica(11);
			break;
		case T_NUMERO:
			buscaProximoToken();
			acumulaRegraSintaticaReconhecida("<X> ::= [0-9]+('.'[0-9]+)");
			regraSemantica(12);
			break;
		case T_ABRE_PARENTESES: {
			buscaProximoToken();
			e();
			if (token == T_FECHA_PARENTESES) {
				buscaProximoToken();
				acumulaRegraSintaticaReconhecida("<X> ::= '(' <E> ')'");
			} else {
				registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: "
						+ colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n')' esperado mas encontrei: " + lexema);
			}
			regraSemantica(13);
		}
		break;
		default:
			registraErroSintatico("Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: "
					+ colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\nFator invalido: encontrei: " + lexema);
		}
	}

	// <ID> ::= [A-Z]+([A-Z]_[0-9])*
	private static void id() throws IOException, ErroLexicoException, ErroSintaticoException, ErroSemanticoException {
		if (token == T_ID) {
			buscaProximoToken();
			acumulaRegraSintaticaReconhecida("<ID> ::= [A-Z]+([A-Z]_[0-9])*");
		} else {
			registraErroSintatico("Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\nEsperava um identificador. Encontrei: " + lexema);
		}
	}

	static void fechaFonte() throws IOException {
		rdFonte.close();
	}

	static void movelookAhead() throws IOException {
		if ((ponteiro + 1) > linhaFonte.length()) {
			linhaAtual++;
			ponteiro = 0;

			if ((linhaFonte = rdFonte.readLine()) == null) {
				lookAhead = FIM_ARQUIVO;
			} else {
				StringBuffer sbLinhaFonte = new StringBuffer(linhaFonte);
				sbLinhaFonte.append('\13').append('\10');
				linhaFonte = sbLinhaFonte.toString();

				lookAhead = linhaFonte.charAt(ponteiro);
			}
		} else {
			lookAhead = linhaFonte.charAt(ponteiro);
		}

		// Se comentar esse if, eu terei uma linguagem
		// que diferencia minusculas de maiusculas
		if ((lookAhead >= 'a') && (lookAhead <= 'z')) {
			lookAhead = (char) (lookAhead - 'a' + 'A');
		}

		ponteiro++;
		colunaAtual = ponteiro + 1;
	}

	static void buscaProximoToken() throws IOException, ErroLexicoException {
		int i, j;
	    
		if ( lexema != null ) {
	        ultimoLexema = new String( lexema );
	    }

		StringBuffer sbLexema = new StringBuffer("");

		// Salto espaçoes enters e tabs até o inicio do proximo token
		while ((lookAhead == 9) || 
				(lookAhead == '\n') || 
				(lookAhead == 8) || 
				(lookAhead == 11) || 
				(lookAhead == 12) || 
				(lookAhead == '\r') || 
				(lookAhead == 32)) {
			movelookAhead();
		}

		/*--------------------------------------------------------------*
		 * Caso o primeiro caracter seja alfabetico, procuro capturar a *
		 * sequencia de caracteres que se segue a ele e classifica-la   *
		 *--------------------------------------------------------------*/
		if ((lookAhead >= 'A') && (lookAhead <= 'Z')) {
			sbLexema.append(lookAhead);
			movelookAhead();

			while (((lookAhead >= 'A') && (lookAhead <= 'Z')) || 
				  ((lookAhead >= '0') && (lookAhead <= '9')) || (lookAhead == '_')) {
				sbLexema.append(lookAhead);
				movelookAhead();
			}

			lexema = sbLexema.toString();

			/* Classifico o meu token como palavra reservada ou id */
			if (lexema.equals("ALGORITMO"))
				token = T_ALGORITMO;
			else if (lexema.equals("INICIO_VARIAVEIS"))
				token = T_INICIO_VARIAVEIS;
			else if (lexema.equals("FIM_VARIAVEIS"))
				token = T_FIM_VARIAVEIS;
			else if (lexema.equals("INTEIRO"))
				token = T_INTEIRO;
			else if (lexema.equals("DECIMAL"))
				token = T_DECIMAL;
			else if (lexema.equals("INICIO"))
				token = T_INICIO;
			else if (lexema.equals("FIM"))
				token = T_FIM;
			else if (lexema.equals("SE"))
				token = T_SE;
			else if (lexema.equals("ENTAO"))
				token = T_ENTAO;
			else if (lexema.equals("SENAO"))
				token = T_SE_NAO;
			else if (lexema.equals("FIM_SE"))
				token = T_FIM_SE;
			else if (lexema.equals("ENQUANTO"))
				token = T_ENQUANTO;
			else if (lexema.equals("FACA"))
				token = T_FACA;
			else if (lexema.equals("FIM_ENQUANTO"))
				token = T_FIM_ENQUANTO;
			else if (lexema.equals("LEIA"))
				token = T_LEIA;
			else if (lexema.equals("ESCREVA"))
				token = T_ESCREVA;
			else {
				token = T_ID;
			}
		} else if ((lookAhead >= '0') && (lookAhead <= '9')) {
			sbLexema.append(lookAhead);
			movelookAhead();
			while ((lookAhead >= '0') && (lookAhead <= '9')) {
				sbLexema.append(lookAhead);
				movelookAhead();
			}
			token = T_NUMERO;
		} else if (lookAhead == '(') {
			sbLexema.append(lookAhead);
			token = T_ABRE_PARENTESES;
			movelookAhead();
		} else if (lookAhead == ')') {
			sbLexema.append(lookAhead);
			token = T_FECHA_PARENTESES;
			movelookAhead();
		} else if (lookAhead == '[') {
			sbLexema.append(lookAhead);
			token = T_ABRE_COLCHETES;
			movelookAhead();
		} else if (lookAhead == ']') {
			sbLexema.append(lookAhead);
			token = T_FECHA_COLCHETES;
			movelookAhead();
		} else if (lookAhead == ';') {
			sbLexema.append(lookAhead);
			token = T_PONTO_VIRGULA;
			movelookAhead();
		} else if (lookAhead == ':') {
			sbLexema.append(lookAhead);
			token = T_DOIS_PONTOS;
			movelookAhead();
		} else if (lookAhead == ',') {
			sbLexema.append(lookAhead);
			token = T_VIRGULA;
			movelookAhead();
		} else if (lookAhead == '+') {
			sbLexema.append(lookAhead);
			token = T_MAIS;
			movelookAhead();
		} else if (lookAhead == '-') {
			sbLexema.append(lookAhead);
			token = T_MENOS;
			movelookAhead();
		} else if (lookAhead == '*') {
			sbLexema.append(lookAhead);
			movelookAhead();
			if (lookAhead == '*') {
				sbLexema.append(lookAhead);
				movelookAhead();
				token = T_ELEVADO;
			} else {
				token = T_VEZES;
			}
		} else if (lookAhead == '/') {
			sbLexema.append(lookAhead);
			token = T_DIVIDIDO;
			movelookAhead();
		} else if (lookAhead == '%') {
			sbLexema.append(lookAhead);
			token = T_RESTO;
			movelookAhead();
		} else if ( lookAhead == '@' ){
	        sbLexema.append( lookAhead );
	        token = T_SOMA_DOBRA;    	
	        movelookAhead();
		} else if ( lookAhead == '$' ){
	        sbLexema.append( lookAhead );
	        token = T_DIVIDE_DOBRA;    	
	        movelookAhead();
		} else if (lookAhead == '\'') {
			sbLexema.append(lookAhead);
			token = T_ASPAS_SIMPLES;
			movelookAhead();
		} else if (lookAhead == '<') {
			sbLexema.append(lookAhead);
			movelookAhead();
			if (lookAhead == '=') {
				sbLexema.append(lookAhead);
				movelookAhead();
				token = T_MENOR_IGUAL;
			} else {
				token = T_MENOR;
			}
		} else if (lookAhead == '>') {
			sbLexema.append(lookAhead);
			movelookAhead();
			if (lookAhead == '=') {
				sbLexema.append(lookAhead);
				movelookAhead();
				token = T_MAIOR_IGUAL;
			} else {
				token = T_MAIOR;
			}
		} else if (lookAhead == '=') {
			sbLexema.append(lookAhead);
			movelookAhead();
			if (lookAhead == '=') {
				sbLexema.append(lookAhead);
				movelookAhead();
				token = T_IGUAL;
			} else {
				token = T_RECEBER;
			}
		} else if (lookAhead == '!') {
			sbLexema.append(lookAhead);
			movelookAhead();
			if (lookAhead == '=') {
				sbLexema.append(lookAhead);
				movelookAhead();
				token = T_DIFERENTE;
			} else {
				token = T_NAO;
			}
		} else if (lookAhead == FIM_ARQUIVO) {
			token = T_FIM_FONTE;
		} else {
			token = T_ERRO_LEX;
			sbLexema.append(lookAhead);
		}

		lexema = sbLexema.toString();

		mostraToken();

		if (token == T_ERRO_LEX) {
			mensagemDeErro = "Erro Léxico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\nToken desconhecido: " + lexema;
			throw new ErroLexicoException(mensagemDeErro);
		}
	}

	static void mostraToken() {

		StringBuffer tokenLexema = new StringBuffer("");
		switch (token) {
		case T_ALGORITMO:
			tokenLexema.append("T_ALGORITMO");
			break;
		case T_PONTO_VIRGULA:
			tokenLexema.append("T_PONTO_VIRGULA");
			break;
		case T_INICIO_VARIAVEIS:
			tokenLexema.append("T_INICIO_VARIAVEIS");
			break;
		case T_FIM_VARIAVEIS:
			tokenLexema.append("T_FIM_VARIAVEIS");
			break;
		case T_DOIS_PONTOS:
			tokenLexema.append("T_DOIS_PONTOS");
			break;
		case T_INTEIRO:
			tokenLexema.append("T_INTEIRO");
			break;
		case T_DECIMAL:
			tokenLexema.append("T_DECIMAL");
			break;
		case T_INICIO:
			tokenLexema.append("T_INICIO");
			break;
		case T_FIM:
			tokenLexema.append("T_FIM");
			break;
		case T_RECEBER:
			tokenLexema.append("T_RECEBER");
			break;
		case T_ABRE_COLCHETES:
			tokenLexema.append("T_ABRE_COLCHETES");
			break;
		case T_FECHA_COLCHETES:
			tokenLexema.append("T_FECHA_COLCHETES");
			break;
		case T_SE:
			tokenLexema.append("T_SE");
			break;
		case T_ENTAO:
			tokenLexema.append("T_ENTAO");
			break;
		case T_SE_NAO:
			tokenLexema.append("T_SE_NAO");
			break;
		case T_FIM_SE:
			tokenLexema.append("T_FIM_SE");
			break;
		case T_ENQUANTO:
			tokenLexema.append("T_ENQUANTO");
			break;
		case T_FACA:
			tokenLexema.append("T_FACA");
			break;
		case T_FIM_ENQUANTO:
			tokenLexema.append("T_FIM_ENQUANTO");
			break;
		case T_LEIA:
			tokenLexema.append("T_LEIA");
			break;
		case T_ABRE_PARENTESES:
			tokenLexema.append("T_ABRE_PARENTESES");
			break;
		case T_FECHA_PARENTESES:
			tokenLexema.append("T_FECHA_PARENTESES");
			break;
		case T_ESCREVA:
			tokenLexema.append("T_ESCREVA");
			break;
		case T_VIRGULA:
			tokenLexema.append("T_VIRGULA");
			break;
		case T_IGUAL:
			tokenLexema.append("T_IGUAL");
			break;
		case T_DIFERENTE:
			tokenLexema.append("T_DIFERENTE");
			break;
		case T_MAIOR:
			tokenLexema.append("T_MAIOR");
			break;
		case T_MAIOR_IGUAL:
			tokenLexema.append("T_MAIOR_IGUAL");
			break;
		case T_MENOR:
			tokenLexema.append("T_MENOR");
			break;
		case T_MENOR_IGUAL:
			tokenLexema.append("T_MENOR_IGUAL");
			break;
		case T_MAIS:
			tokenLexema.append("T_MAIS");
			break;
		case T_MENOS:
			tokenLexema.append("T_MENOS");
			break;
		case T_VEZES:
			tokenLexema.append("T_VEZES");
			break;
		case T_DIVIDIDO:
			tokenLexema.append("T_DIVIDIDO");
			break;
		case T_RESTO:
			tokenLexema.append("T_RESTO");
			break;
		case T_SOMA_DOBRA: 
			tokenLexema.append( "T_SOMA_DOBRA" ); 
			break;
		case T_DIVIDE_DOBRA: 
			tokenLexema.append( "T_DIVIDE_DOBRA" ); 
			break;
		case T_ELEVADO:
			tokenLexema.append("T_ELEVADO");
			break;
		case T_NAO:
			tokenLexema.append("T_NAO");
			break;
		case T_ASPAS_SIMPLES:
			tokenLexema.append("T_ASPAS_SIMPLES");
			break;
		case T_ID:
			tokenLexema.append("T_ID");
			break;
		case T_NUMERO:
			tokenLexema.append("T_NUMERO");
			break;
		case T_FIM_FONTE:
			tokenLexema.append("T_FIM_FONTE");
			break;
		case T_ERRO_LEX:
			tokenLexema.append("T_ERRO_LEX");
			break;
		case T_NULO:
			tokenLexema.append("T_NULO");
			break;
		default:
			tokenLexema.append("N/A");
			break;
		}
		System.out.println(tokenLexema.toString() + " ( " + lexema + " )");
		acumulaToken(tokenLexema.toString() + " ( " + lexema + " )");
		tokenLexema.append(lexema);
	}

	private static void abreArquivo() {
		JFileChooser fileChooser = new JFileChooser();

		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		FiltroSab filtro = new FiltroSab();

		fileChooser.addChoosableFileFilter(filtro);
		int result = fileChooser.showOpenDialog(null);

		if (result == JFileChooser.CANCEL_OPTION) {
			return;
		}

		arqFonte = fileChooser.getSelectedFile();
		abreFonte(arqFonte);

	}

	private static boolean abreFonte(File fileName) {
		if (arqFonte == null || fileName.getName().trim().equals("")) {
			JOptionPane.showMessageDialog(null, "Nome de Arquivo Invalido", "Nome de Arquivo Invalido",
					JOptionPane.ERROR_MESSAGE);
			return false;
		} else {
			linhaAtual = 1;
			try {
				FileReader fr = new FileReader(arqFonte);
				rdFonte = new BufferedReader(fr);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return true;
		}
	}

	private static void abreDestino() {
		JFileChooser fileChooser = new JFileChooser();

		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		FiltroSab filtro = new FiltroSab();

		fileChooser.addChoosableFileFilter(filtro);
		int result = fileChooser.showSaveDialog(null);

		if (result == JFileChooser.CANCEL_OPTION) {
			return;
		}

		arqDestino = fileChooser.getSelectedFile();
	}

	private static boolean gravaSaida( File fileName ) {
		if( arqDestino == null || fileName.getName().trim().equals( "" ) ) {
			JOptionPane.showMessageDialog( null, "Nome de Arquivo Invalido", "Nome de Arquivo Invalido", JOptionPane.ERROR_MESSAGE );
			return false;
		} else {
			FileWriter fw;
			try {
				fw = new FileWriter( arqDestino );
				BufferedWriter bfw = new BufferedWriter( fw ); 
                bfw.write( codigoPython.toString() );
                bfw.close();
				JOptionPane.showMessageDialog( null, "Arquivo Salvo: " + arqDestino, "Salvando Arquivo", JOptionPane.INFORMATION_MESSAGE );
			} catch (IOException e) {
				JOptionPane.showMessageDialog( null, e.getMessage(), "Erro de Entrada/Saida", JOptionPane.ERROR_MESSAGE );
			} 
			return true;
		}
	}

	public static void exibeTokens() {
		JTextArea texto = new JTextArea();
		texto.append(tokensIdentificados.toString());
		JOptionPane.showMessageDialog(null, texto, "Tokens Identificados (token/lexema)", JOptionPane.INFORMATION_MESSAGE);
	}

	public static void acumulaRegraSintaticaReconhecida(String regra) {
		regrasReconhecidas.append(regra);
		regrasReconhecidas.append("\n");
	}

	public static void acumulaToken(String tokenIdentificado) {
		tokensIdentificados.append(tokenIdentificado);
		tokensIdentificados.append("\n");
	}

	public static void exibeSaida() {
		JTextArea texto = new JTextArea();
		texto.append(tokensIdentificados.toString());
		JOptionPane.showMessageDialog(null, texto, "Analise Lexica", JOptionPane.INFORMATION_MESSAGE);

		texto.setText(regrasReconhecidas.toString());
		texto.append("\n\nStatus da Compilacao:\n\n");
		texto.append(mensagemDeErro);

		JOptionPane.showMessageDialog(null, texto, "Resumo da Compilacao", JOptionPane.INFORMATION_MESSAGE);
	}

	static void registraErroSintatico(String msg) throws ErroSintaticoException {
		if (estadoCompilacao == E_SEM_ERROS) {
			estadoCompilacao = E_ERRO_SINTATICO;
			mensagemDeErro = msg;
		}
		throw new ErroSintaticoException(msg);
	}
	
	static void registraErroSemantico( String msg ) {
		if ( estadoCompilacao == E_SEM_ERROS ) {
            estadoCompilacao = E_ERRO_SEMANTICO;
            mensagemDeErro = msg;
        }
    }
	
	static void regraSemantica( int numeroRegra ) throws ErroSemanticoException {
        System.out.println( "Regra Semantica " + numeroRegra );
        switch ( numeroRegra ) {
            case  0: 	codigoPython.append( "import os\nimport sys\nimport glob\nimport string\n\n" );
                		codigoPython.append( "def main( ):\n" );
                		nivelIdentacao++;
                		break;
            case  1: 	codigoPython.append( tabulacao( nivelIdentacao ) );
                		codigoPython.append( "pass\n\n" );
                		codigoPython.append( "if __name__ == '__main__':\n" );
                		codigoPython.append( tabulacao( nivelIdentacao ) );
                		codigoPython.append( "main( )\n" );
                		break;
            case  2:	insereNaTabelaSimbolos( ultimoLexema );
						break;
            case  3:	nodo_2 = pilhaSemantica.pop();
						nodo_1 = pilhaSemantica.pop();	
						System.out.println( "Codigo 1 " + nodo_1.getCodigo() );
						System.out.println( "Codigo 2 " + nodo_2.getCodigo() );
						codigoPython.append( tabulacao( nivelIdentacao ) );
						codigoPython.append( nodo_1.getCodigoMinusculo() + " = " + nodo_2.getCodigoMinusculo() + "\n" );
						break;
            case  4:	if ( VeSeExisteNaTabelaSimbolos( ultimoLexema ) ) {
				            pilhaSemantica.push( ultimoLexema, 4 );
			            }
			            break;
            case  5:    nodo_2 = pilhaSemantica.pop();
						nodo_1 = pilhaSemantica.pop();
						pilhaSemantica.push( nodo_1.getCodigoMinusculo() + "+" + nodo_2.getCodigoMinusculo(), 5 );
						break;
            case  6:    nodo_2 = pilhaSemantica.pop();
						nodo_1 = pilhaSemantica.pop();
						pilhaSemantica.push( nodo_1.getCodigoMinusculo() + "-" + nodo_2.getCodigoMinusculo(), 6 );
						break;
            case  7:    nodo_2 = pilhaSemantica.pop();
						nodo_1 = pilhaSemantica.pop();
						pilhaSemantica.push( nodo_1.getCodigoMinusculo() + "*" + nodo_2.getCodigoMinusculo(), 7 );
						break;
            case  8:    nodo_2 = pilhaSemantica.pop();
						nodo_1 = pilhaSemantica.pop();
						pilhaSemantica.push( nodo_1.getCodigoMinusculo() + "/" + nodo_2.getCodigoMinusculo(), 8 );
						break;
            case  9:    nodo_2 = pilhaSemantica.pop();
						nodo_1 = pilhaSemantica.pop();
						pilhaSemantica.push( nodo_1.getCodigoMinusculo() + "%" + nodo_2.getCodigoMinusculo(), 9 );
						break;
            case 10:    nodo_2 = pilhaSemantica.pop();
            			nodo_1 = pilhaSemantica.pop();
            			pilhaSemantica.push( nodo_1.getCodigoMinusculo() + "**" + nodo_2.getCodigoMinusculo(), 10 );
            			break;
            case 11:	if ( VeSeExisteNaTabelaSimbolos( ultimoLexema ) ) {
	            			pilhaSemantica.push( ultimoLexema, 11 );
            			}
            			break;
            case 12:	pilhaSemantica.push( ultimoLexema, 12 );
            			break;
            case 13:	nodo_1 = pilhaSemantica.pop();
            			pilhaSemantica.push( "(" + nodo_1.getCodigoMinusculo() + ")" , 13 );            
            			break;
            case 14:    nodo_1 = pilhaSemantica.pop();
    					codigoPython.append( tabulacao( nivelIdentacao ) );
    					codigoPython.append( nodo_1.getCodigoMinusculo() + " = int( input('Informe a variavel " + nodo_1.getCodigoMinusculo() + " ') )\n" );
    					break;
            case 15:    nodo_1 = pilhaSemantica.pop();
						codigoPython.append( tabulacao( nivelIdentacao ) );
						codigoPython.append( "while ( " + nodo_1.getCodigoMinusculo() + " ):\n" );
						nivelIdentacao++;
						break;
            case 16:    nivelIdentacao--;
						break;
            case 17:    nodo_1 = pilhaSemantica.pop();
						codigoPython.append( tabulacao( nivelIdentacao ) );
						codigoPython.append( "if " + nodo_1.getCodigoMinusculo() + ":\n" );
						nivelIdentacao++;
						break;
            case 18:    codigoPython.append( tabulacao( nivelIdentacao ) );
						codigoPython.append( "else:\n" );
						nivelIdentacao++;
						break;
            case 19:    nodo_2 = pilhaSemantica.pop();
						nodo_1 = pilhaSemantica.pop();
						pilhaSemantica.push( nodo_1.getCodigoMinusculo() + " > " + nodo_2.getCodigoMinusculo(), 19 );
						break;						
            case 20:    nodo_2 = pilhaSemantica.pop();
						nodo_1 = pilhaSemantica.pop();
						pilhaSemantica.push( nodo_1.getCodigoMinusculo() + " < " + nodo_2.getCodigoMinusculo(), 20 );
						break;						
            case 21:    nodo_2 = pilhaSemantica.pop();
						nodo_1 = pilhaSemantica.pop();
						pilhaSemantica.push( nodo_1.getCodigoMinusculo() + " >= " + nodo_2.getCodigoMinusculo(), 21 );
						break;						
            case 22:    nodo_2 = pilhaSemantica.pop();
						nodo_1 = pilhaSemantica.pop();
						pilhaSemantica.push( nodo_1.getCodigoMinusculo() + " <= " + nodo_2.getCodigoMinusculo(), 22 );
						break;						
            case 23:    nodo_2 = pilhaSemantica.pop();
						nodo_1 = pilhaSemantica.pop();
						pilhaSemantica.push( nodo_1.getCodigoMinusculo() + " == " + nodo_2.getCodigoMinusculo(), 23 );
						break;						
            case 24:    nodo_2 = pilhaSemantica.pop();
						nodo_1 = pilhaSemantica.pop();
						pilhaSemantica.push( nodo_1.getCodigoMinusculo() + " != " + nodo_2.getCodigoMinusculo(), 24 );
						break;		
            case 25:    nodo_1 = pilhaSemantica.pop();
    					codigoPython.append( tabulacao( nivelIdentacao ) );
    					codigoPython.append( "print ( " + nodo_1.getCodigoMinusculo() + " )\n" );
    					break;
            case 26:    nodo_1 = pilhaSemantica.pop();
            			codigoPython.append( tabulacao( nivelIdentacao ) );
    					codigoPython.append( nodo_1.getCodigoMinusculo() + "=0\n" );
    					break;
            case 27:    nodo_2 = pilhaSemantica.pop();
						nodo_1 = pilhaSemantica.pop();
						pilhaSemantica.push( "2 * ( " + nodo_1.getCodigoMinusculo() + "+" + nodo_2.getCodigoMinusculo() + ") ", 27 );
						break;
            case 28:    nodo_2 = pilhaSemantica.pop();
						nodo_1 = pilhaSemantica.pop();
						pilhaSemantica.push( "( " + nodo_1.getCodigoMinusculo() + "/" + nodo_2.getCodigoMinusculo() + ") * 2", 28 );
						break;
        }
	}
	
	private static int buscaTipoNaTabelaSimbolos(String ultimoLexema ) throws ErroSemanticoException {
    	return tabelaSimbolos.get( ultimoLexema );
	}
    
    private static boolean VeSeExisteNaTabelaSimbolos(String ultimoLexema ) throws ErroSemanticoException {
    	if ( !tabelaSimbolos.containsKey( ultimoLexema ) ) {
	    	throw new ErroSemanticoException( "Variavel " + ultimoLexema + " nao esta declarada! linha: " + linhaAtual );
    	} else {
    		return true;
    	}
	}

	private static void insereNaTabelaSimbolos(String ultimoLexema) throws ErroSemanticoException {
		if ( tabelaSimbolos.containsKey( ultimoLexema ) ) {
	    	throw new ErroSemanticoException( "Variavel " + ultimoLexema + " ja declarada! linha: " + linhaAtual );
		} else {
			tabelaSimbolos.put( ultimoLexema, 0 );
		}
	}
    
	static String tabulacao( int qtd ) {
		StringBuffer sb = new StringBuffer();
        for ( int i=0; i<qtd; i++ ) {
            sb.append( "    " );
        }
        return sb.toString();
    }
}

/**
 * Classe Interna para criacao de filtro de selecao
 */
class FiltroSab extends FileFilter {

	public boolean accept(File arg0) {
		if (arg0 != null) {
			if (arg0.isDirectory()) {
				return true;
			}
			if (getExtensao(arg0) != null) {
				if (getExtensao(arg0).equalsIgnoreCase("grm")) {
					return true;
				}
			}
			;
		}
		return false;
	}

	/**
	 * Retorna quais extensoes poderao ser escolhidas
	 */
	public String getDescription() {
		return "*.grm";
	}

	/**
	 * Retorna a parte com a extensao de um arquivo
	 */
	public String getExtensao(File arq) {
		if (arq != null) {
			String filename = arq.getName();
			int i = filename.lastIndexOf('.');
			if (i > 0 && i < filename.length() - 1) {
				return filename.substring(i + 1).toLowerCase();
			}
			;
		}
		return null;
	}
}