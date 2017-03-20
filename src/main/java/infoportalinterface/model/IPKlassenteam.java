package infoportalinterface.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by martin on 20.03.2017.
 */
public class IPKlassenteam {

    private Map<IPFachEnum, List<IPLehrkraft>> kt = new HashMap<>();

    public void add(IPLehrkraft lehrkraft, IPFachEnum fach){

        List<IPLehrkraft> lehrkraefteImFach = kt.get(fach);

        if(lehrkraefteImFach == null){
            lehrkraefteImFach = new ArrayList<>();
            kt.put(fach, lehrkraefteImFach);
        }

        if(!lehrkraefteImFach.contains(lehrkraft)){
            lehrkraefteImFach.add(lehrkraft);
        }

    }




}
