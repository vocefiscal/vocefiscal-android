/**
 * 
 */
package org.vocefiscal.models.enums;

/**
 * @author andre
 *
 */
public enum BrazilStateCodesEnum 
{
	ACRE,ALAGOAS,AMAZONAS,AMAPA,BAHIA,CEARA,DISTRITO_FEDERAL,ESPIRITO_SANTO,GOIAS,MARANHAO,MINAS_GERAIS,MATO_GROSSO_DO_SUL,
	MATO_GROSSO,PARA,PARAIBA,PERNAMBUCO,PIAUI,PARANA,RIO_DE_JANEIRO,RIO_GRANDE_DO_NORTE,RONDONIA,RORAIMA,RIO_GRANDE_DO_SUL,SANTA_CATARINA,
	SERGIPE,SAO_PAULO,TOCANTINS;

	public static String getStateCode(String nomeDoEstado) 
	{
		String stateCode = null;

		if(nomeDoEstado.equalsIgnoreCase("Acre"))
		{
			stateCode = "AC";
		}else if(nomeDoEstado.equalsIgnoreCase("Alagoas"))
		{
			stateCode = "AL";
		}else if(nomeDoEstado.equalsIgnoreCase("Amazonas"))
		{
			stateCode = "AM";
		}else if(nomeDoEstado.equalsIgnoreCase("Amapá")||nomeDoEstado.equalsIgnoreCase("Amapa"))
		{
			stateCode = "AP";
		}else if(nomeDoEstado.equalsIgnoreCase("Bahia"))
		{
			stateCode = "BA";
		}else if(nomeDoEstado.equalsIgnoreCase("Ceará")||nomeDoEstado.equalsIgnoreCase("Ceara"))
		{
			stateCode = "CE";
		}else if(nomeDoEstado.equalsIgnoreCase("Distrito Federal"))
		{
			stateCode = "DF";
		}else if(nomeDoEstado.equalsIgnoreCase("Espírito Santo")||nomeDoEstado.equalsIgnoreCase("Espirito Santo"))
		{
			stateCode = "ES";
		}else if(nomeDoEstado.equalsIgnoreCase("Goiás")||nomeDoEstado.equalsIgnoreCase("Goias"))
		{
			stateCode = "GO";
		}else if(nomeDoEstado.equalsIgnoreCase("Maranhão")||nomeDoEstado.equalsIgnoreCase("Maranhao"))
		{
			stateCode = "MA";
		}else if(nomeDoEstado.equalsIgnoreCase("Minas Gerais"))
		{
			stateCode = "MG";
		}else if(nomeDoEstado.equalsIgnoreCase("Mato Grosso do SUl"))
		{
			stateCode = "MS";
		}else if(nomeDoEstado.equalsIgnoreCase("Mato Grosso"))
		{
			stateCode = "MT";
		}else if(nomeDoEstado.equalsIgnoreCase("Pará")||nomeDoEstado.equalsIgnoreCase("Para"))
		{
			stateCode = "PA";
		}else if(nomeDoEstado.equalsIgnoreCase("Paraíba")||nomeDoEstado.equalsIgnoreCase("Paraiba"))
		{
			stateCode = "PB";
		}else if(nomeDoEstado.equalsIgnoreCase("Pernambuco"))
		{
			stateCode = "PE";
		}else if(nomeDoEstado.equalsIgnoreCase("Piauí")||nomeDoEstado.equalsIgnoreCase("Piaui"))
		{
			stateCode = "PI";
		}else if(nomeDoEstado.equalsIgnoreCase("Paraná")||nomeDoEstado.equalsIgnoreCase("Parana"))
		{
			stateCode = "PR";
		}else if(nomeDoEstado.equalsIgnoreCase("Rio de Janeiro"))
		{
			stateCode = "RJ";
		}else if(nomeDoEstado.equalsIgnoreCase("Rio Grande do Norte"))
		{
			stateCode = "RN";
		}else if(nomeDoEstado.equalsIgnoreCase("Rondônia")||nomeDoEstado.equalsIgnoreCase("Rondonia"))
		{
			stateCode = "RO";
		}else if(nomeDoEstado.equalsIgnoreCase("Roraima"))
		{
			stateCode = "RR";
		}else if(nomeDoEstado.equalsIgnoreCase("Rio Grande do Sul"))
		{
			stateCode = "RS";
		}else if(nomeDoEstado.equalsIgnoreCase("Santa Catarina"))
		{
			stateCode = "SC";
		}else if(nomeDoEstado.equalsIgnoreCase("Sergipe"))
		{
			stateCode = "SE";
		}else if(nomeDoEstado.equalsIgnoreCase("São Paulo")||nomeDoEstado.equalsIgnoreCase("Sao Paulo"))
		{
			stateCode = "SP";
		}else if(nomeDoEstado.equalsIgnoreCase("Tocantins"))
		{
			stateCode = "TO";
		}

		return stateCode;
	}

	public String toString()
	{		
		switch (this) 
		{
		case ACRE:
			return "Acre";
		case ALAGOAS:
			return "Alagoas";
		case AMAZONAS:
			return "Amazonas";
		case AMAPA:
			return "Amapá";
		case BAHIA:
			return "Bahia";
		case CEARA:
			return "Ceará";
		case DISTRITO_FEDERAL:
			return "Distrito Federal";
		case ESPIRITO_SANTO:
			return "Espírito Santo";
		case GOIAS:
			return "Goiás";
		case MARANHAO:
			return "Maranhão";
		case MINAS_GERAIS:
			return "Minas Gerais";
		case MATO_GROSSO_DO_SUL:
			return "Mato Grosso do Sul";
		case MATO_GROSSO:
			return "Mato Grosso";
		case PARA:
			return "Pará";
		case PARAIBA:
			return "Paraíba";
		case PERNAMBUCO:
			return "Pernambuco";
		case PIAUI:
			return "Piauí";
		case PARANA:
			return "Paraná";
		case RIO_DE_JANEIRO:
			return "Rio de Janeiro";
		case RIO_GRANDE_DO_NORTE:
			return "Rio Grande do Norte";
		case RONDONIA:
			return "Rondônia";
		case RORAIMA:
			return "Roraima";
		case RIO_GRANDE_DO_SUL:
			return "Rio Grande do Sul";
		case SANTA_CATARINA:
			return "Santa Catarina";
		case SERGIPE:
			return "Sergipe";
		case SAO_PAULO:
			return "São Paulo";
		case TOCANTINS:
			return "Tocantins";

		default : 
			return "São Paulo";
		}

	}

}
