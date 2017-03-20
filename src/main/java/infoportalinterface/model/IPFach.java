package infoportalinterface.model;

import java.util.ArrayList;
import java.util.List;

public class IPFach {

	private IPFachEnum fachEnum = null;

	private IPNote jZ; // autorisierte JZ-Note
	private IPNote zZ; // autorisierte ZZ-Note
	private IPNote sG; // Durchschnitt gesamt
	private List<IPNote> schulaufgabenNoten = new ArrayList<>();
	private IPNote durchschnittGL; // Durchschnitt der großen Leistungsnachweise
	private IPNote durchschnittKL; // Durchschnitt der kleinen

	@Override
	public String toString() {
		if(sG == null){
			return "";
		} else {
			StringBuilder sb = new StringBuilder();

			sb.append(fachEnum.getKurzform() + "(");
			if(jZ != null){
				sb.append("JZ: " + jZ.getValue() + "; ");
			}
			if(zZ != null){
				sb.append("ZZ: " + zZ.getValue() + "; ");
			}
			if(sG != null){
				sb.append("SG: " + sG.getValue() + "; ");
			}
			if(schulaufgabenNoten.size() >= 0){
					sb.append("SchA: ");
				for(IPNote n: schulaufgabenNoten){
					sb.append(n.toString() + " ");
				}
					sb.append("; ");
			}
			if(durchschnittGL != null){
				sb.append("SchnittGL: " + durchschnittGL.getValue() + "; ");
			}

            if(durchschnittGL != null){
                sb.append("SchnittGL: " + durchschnittGL.getValue() + "; ");
            }
			if(durchschnittKL != null){
				sb.append("SchnittKL: " + durchschnittKL.getValue() + "; ");
			}

			sb.append(")");
			return sb.toString();
		}
	}



	public IPFach(IPFachEnum ipfe) {

		this.fachEnum = ipfe;

	}

	// Leistungsnachweise
	public IPNote getjZ() {
		return jZ;
	}
	public IPNote getzZ() {
		return zZ;
	}
	public IPNote getsG() {
		return sG;
	}
	public List<IPNote> getSchulaufgabenNoten() {
		return schulaufgabenNoten;
	}
	public IPNote getDurchschnittGL() {
		return durchschnittGL;
	}
	public IPNote getDurchschnittKL() {
		return durchschnittKL;
	}

	public IPFachEnum getFachEnum() {
		return fachEnum;
	}

	public boolean hasFach(IPFachEnum ipfe){
		return fachEnum == ipfe;
	}

	public void setNoten(IPNote jz, IPNote zz, IPNote sg, List<IPNote> schulaufgaben, IPNote gl, IPNote kl) {

		this.jZ = jz;
		this.zZ = zz;
		this.sG = sg;
		this.schulaufgabenNoten = schulaufgaben;
		this.durchschnittGL = gl;
		this.durchschnittKL = kl;

	}
}
