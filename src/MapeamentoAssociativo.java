import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class MapeamentoAssociativo {

    private Map<Integer, String> tamanhocache;
    private Map<Integer, Integer> auxiliocache;
    private ArrayList<Integer> listaTagsCache;
    private ArrayList<String> tamanhomemoria;
    private ArrayList<Integer> listaauxiliarcache;
    private Long tamanhoendereco;
    private Long tamanhotag;
    private Integer tamanholinha = 0;
    private Integer tamanhopalavra;
    private Long limitedacache;
    private int acertos;
    private int erros;
    private int sub;

    public MapeamentoAssociativo(ArrayList<String> tamanhomemoria, ArrayList<String> dados, int sub) {
        this.tamanhomemoria = tamanhomemoria;
        this.sub = sub;

        String[] memoria = dados.get(0).split("[ #@_\\/.*;]");
        String[] palavra = dados.get(1).split("[ #@_\\/.*;]");
        String[] cache = dados.get(2).split("[ #@_\\/.*;]");
        String[] configuracao = dados.get(3).split("[ #@_\\/.*;]");

        Long memoriaBytes = converterBytes(Long.parseLong(memoria[2]), memoria[3]);
        Long palavraBytes = converterBytes(Long.parseLong(palavra[2]), palavra[3]);
        Long cacheBytes = converterBytes(Long.parseLong(cache[2]), cache[3]);

        tamanhocache = new HashMap<>();
        auxiliocache = new HashMap<Integer, Integer>();
        listaTagsCache = new ArrayList<>();
        limitedacache = (cacheBytes / palavraBytes) / Integer.parseInt(configuracao[2]);
        listaauxiliarcache = new ArrayList<Integer>();

        tamanhoendereco = Math.round(Math.log(memoriaBytes / palavraBytes) / Math.log(2));
        tamanhopalavra = (int) Math.round(Math.log(palavraBytes) / Math.log(2));
        tamanhotag = (tamanhoendereco - tamanhopalavra) - tamanholinha;

        metodo();
    }

    protected void metodo() {
        for (String endereco : tamanhomemoria) {
            String enderecoBinario = Conversion.intToBinaryString(Long.parseLong(endereco), tamanhoendereco);
            String tagBinario = enderecoBinario.substring(0, tamanhotag.intValue());
            String pal = enderecoBinario.substring(tamanhotag.intValue());
            Integer tag = Integer.parseInt(tagBinario, 2);

            if (tamanhocache.containsKey(tag)) {
                acertos++;
                if (sub == 1)
                    auxiliocache.compute(tag, (k, v) -> v + 1);
                else if (sub == 3) {
                    listaauxiliarcache.remove(tag);
                    listaauxiliarcache.add(tag);
                }
            } else {
                erros++;
                if (tamanhocache.size() >= limitedacache) {
                    switch (sub) {
                        case 1:
                            lfu();
                            break;
                        case 2:
                            fifo();
                            break;
                        case 3:
                            lru();
                            break;
                        case 4:
                            random();
                            break;
                    }
                }
                tamanhocache.put(tag, pal);
                listaTagsCache.add(tag);
                if (sub == 1) {
                    auxiliocache.put(tag, 0);
                } else if (sub == 3) {
                    listaauxiliarcache.add(tag);
                }
            }
        }
        resultado();
    }

    public void lfu() {
        int minFreq = Integer.MAX_VALUE;
        Integer lfuTag = null;

        for (Map.Entry<Integer, Integer> entry : auxiliocache.entrySet()) {
            if (entry.getValue() < minFreq) {
                minFreq = entry.getValue();
                lfuTag = entry.getKey();
            }
        }

        if (lfuTag != null) {
            tamanhocache.remove(lfuTag);
            auxiliocache.remove(lfuTag);
        }
    }

    public void fifo() {
        Integer primeiraTag = tamanhocache.keySet().iterator().next();
        tamanhocache.remove(primeiraTag);
    }

    public void lru() {
        Integer firstTag = listaauxiliarcache.remove(0);
        tamanhocache.remove(firstTag);
    }

    public void random() {
        int randomIndex = ThreadLocalRandom.current().nextInt(listaTagsCache.size());
        Integer randomTag = listaTagsCache.get(randomIndex);
        tamanhocache.remove(randomTag);
        listaTagsCache.remove(randomIndex);
    }

    private Long converterBytes(Long bytes, String unidade) {
        switch (unidade) {
            case "KB":
                bytes *= 1024;
                break;
            case "MB":
                bytes *= 1024 * 1024;
                break;
            case "GB":
                bytes *= 1024 * 1024 * 1024;
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
