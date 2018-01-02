package br.com.caelum.stella.boleto.bancos;

import static br.com.caelum.stella.boleto.utils.StellaStringUtils.leftPadWithZeros;

import java.net.URL;

import br.com.caelum.stella.boleto.Beneficiario;
import br.com.caelum.stella.boleto.Boleto;
import br.com.caelum.stella.boleto.bancos.gerador.GeradorDeDigitoSantander;

/**
 * Gerar Boleto do Banco Santander
 * 
 */
public class Santander extends AbstractBanco {

	private static final long serialVersionUID = 1L;

	private final static String NUMERO_SANTANDER = "033";
	private final static String DIGITO_SANTANDER = "7";
	private static final String CODIGO_BOLETO_REGISTRADO = "02";
	private static final String CODIGO_BOLETO_REJEITADO = "03";
	
	private GeradorDeDigitoSantander gdivSantander = new GeradorDeDigitoSantander();

	@Override
	public String geraCodigoDeBarrasPara(Boleto boleto) {
		Beneficiario beneficiario = boleto.getBeneficiario();
		StringBuilder campoLivre = new StringBuilder("9");
		campoLivre.append(getNumeroConvenioFormatado(beneficiario));
		campoLivre.append(getNossoNumeroFormatado(beneficiario));
		campoLivre.append("0").append(getCarteiraFormatado(beneficiario));
		return new CodigoDeBarrasBuilder(boleto).comCampoLivre(campoLivre);
	}
	
	@Override
	public URL getImage() {
		String pathDoArquivo = "/br/com/caelum/stella/boleto/img/%s.png";
		String imagem = String.format(pathDoArquivo, NUMERO_SANTANDER);
		return getClass().getResource(imagem);
	}

	@Override
	public String getNumeroFormatado() {
		return NUMERO_SANTANDER;
	}

	@Override
	public String getCarteiraFormatado(Beneficiario beneficiario) {
		return leftPadWithZeros(beneficiario.getCarteira(), 3);
	}

	@Override
	public String getCodigoBeneficiarioFormatado(Beneficiario beneficiario) {
		return leftPadWithZeros(beneficiario.getCodigoBeneficiario(), 7);
	}

	@Override
	public String getNossoNumeroFormatado(Beneficiario beneficiario) {
		String nossoNumero = beneficiario.getNossoNumero();
		if (beneficiario.getDigitoNossoNumero() != null) {
			return leftPadWithZeros(nossoNumero+beneficiario.getDigitoNossoNumero(), 13);
		} 
		return leftPadWithZeros(nossoNumero+getGeradorDeDigito().calculaDVNossoNumero(nossoNumero), 13);
	}

	@Override
	public String getNumeroFormatadoComDigito() {
		StringBuilder builder = new StringBuilder();
		builder.append(NUMERO_SANTANDER).append("-");
		return builder.append(DIGITO_SANTANDER).toString();
	}

	@Override
	public String getAgenciaECodigoBeneficiario(Beneficiario beneficiario) {
		StringBuilder builder = new StringBuilder();
		builder.append(leftPadWithZeros(beneficiario.getAgencia(), 5));
		builder.append("/").append(getNumeroConvenioFormatado(beneficiario));
		return builder.toString();
	}

	@Override
	public String getNossoNumeroECodigoDocumento(Boleto boleto) {
		Beneficiario beneficiario = boleto.getBeneficiario();
		
		String nossoNumero = getNossoNumeroFormatado(beneficiario);
		StringBuilder builder = new StringBuilder();
		builder.append(nossoNumero.substring(0, 12));
		builder.append("-").append(nossoNumero.substring(12));
		return  builder.toString();
	}
 
	@Override
	public GeradorDeDigitoSantander getGeradorDeDigito() {
		return gdivSantander;
	}
	
	public String getNumeroConvenioFormatado(Beneficiario beneficiario) {
		return leftPadWithZeros(beneficiario.getNumeroConvenio(), 7);
	}

     /**
      * Método para gerar o nosso número concatenado com o dígito de controle
      * 
      * @param beneficiario
      * @return String
      */
     @Override
     public String getNossoNumeroComDigitoVerificador(Beneficiario beneficiario) {

          return beneficiario.getNossoNumero() + gerarDigitoControleNossoNumero(beneficiario.getNossoNumero());
     }
     
     /**
      * Método para calcular o dígito de controle do nosso número 
      * 
      * @param nossoNumero
      * @return String
      */
     public String gerarDigitoControleNossoNumero(String nossoNumero) {

          int somaNossoNumero = 0;
          int multiplicador = 2;
          int multiplicacao = 0;

          for (int i = nossoNumero.length() - 1; i >= 0; i--) {

               multiplicacao = Integer.parseInt(nossoNumero.substring(i, 1 + i)) * multiplicador;
               somaNossoNumero = somaNossoNumero + multiplicacao;

               if (multiplicador == 9) {
                    multiplicador = 2;
               } else {
                    multiplicador++;
               }
          }

          int digitoControle = 11 - (somaNossoNumero % 11);
          if (digitoControle > 9) {
               digitoControle = 0;
          }

          return String.valueOf(digitoControle);
     }

     @Override
     public String getCodigoBoletoRegistrado() {

          return CODIGO_BOLETO_REGISTRADO;
     }

     @Override
     public String getCodigoBoletoRejeitado() {

          return CODIGO_BOLETO_REJEITADO;
     }
}