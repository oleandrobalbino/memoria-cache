import java.util.ArrayList;

public  class MapeamentoDireto {

    private String[] tamanhocache;
    private ArrayList<String> tamanhomemoria;
    private Long tamanhoendereco;
    private Long tamanhotag;
    private Integer tamanholinha;
    private Integer tamanhopalavra;
    private Long limitecache;
    private int acertos;
    private int erros;

    public MapeamentoDireto(ArrayList<String> tamanhomemoria, ArrayList<String> dados) {
        this.tamanhomemoria = tamanhomemoria;

        String[] memoria = dados.get(0).split("[ #@_\\/.*;]");
        String[] palavra = dados.get(1).split("[ #@_\\/.*;]");
        String[] cache = dados.get(2).split("[ #@_\\/.*;]");
        String[] linha = dados.get(3).split("[ #@_\\/.*;]");

        Long memoriaBytes = converterBytes(Long.parseLong(memoria[2]), memoria[3]);
        Long palavraBytes = converterBytes(Long.parseLong(palavra[2]), palavra[3]);
        Long cacheBytes = converterBytes(Long.parseLong(cache[2]), cache[3]);

        limitecache = (cacheBytes / palavraBytes) / Integer.parseInt(linha[2]);
        tamanhocache = new String[limitecache.intValue()];

        tamanhoendereco = Math.round(Math.log(memoriaBytes / palavraBytes) / Math.log(2));
        tamanhopalavra = (int) Math.round(Math.log(palavraBytes) / Math.log(2));
        tamanholinha = (int) Math.round(Math.log((cacheBytes / palavraBytes) / Integer.parseInt(linha[2])) / Math.log(2));
        tamanhotag = (tamanhoendereco - tamanhopalavra) - tamanholinha;

        metodo();
    }

    private void metodo() {
        for (int i = 0; i < tamanhomemoria.size(); i++) {
            String enderecoBinario = Conversion.intToBinaryString(Long.parseLong(tamanhomemoria.get(i)),tamanhoendereco);
            String tag = enderecoBinario.substring(0, tamanhotag.intValue());
            String linha = enderecoBinario.substring(tamanhotag.intValue(), tamanholinha + tamanhotag.intValue());
            Integer linhaDecimal = Integer.parseInt(linha, 2);
            if (linhaDecimal < limitecache) {
                if (tamanhocache[linhaDecimal] != null && tamanhocache[linhaDecimal].equals(tag)) {
                    acertos++;
                } else {
                    erros++;
                    tamanhocache[linhaDecimal] = tag;
                }
            }
        }
        resultado();
    }

    private Long converterBytes(Long bytes, String unidade) {
        switch (unidade) {
            case "KB":
                bytes = bytes * 1024;
                break;
            case "MB":
                bytes = bytes * 1024 * 1024;
                break;
            case "GB":
                bytes = bytes * 1024 * 1024 * 1024;
                break;
        }
        return bytes;
    }

    public String resultado() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("O número de acertos foi: ").append(acertos).append("\n");
        sb.append("O número de erros foi: ").append(erros).append("\n");
        double percentualAcertos = (double) acertos / tamanhomemoria.size() * 100;
        sb.append(String.format("O percentual de acerto é: %.2f%%\n", percentualAcertos));
        
        return sb.toString();
    }
    

}

