package com.wi.main.recognition;




import com.wi.main.domain.Term;
import com.wi.main.domain.TermNature;
import com.wi.main.domain.TermNatures;
import com.wi.main.library.UserDefineLibrary;
import com.wi.main.util.TermUtil;
import com.wi.tire.domain.Forest;
import com.wi.tire.domain.WoodInterface;
import com.wi.tire.util.ObjectBean;


/**
 * 用户自定义词典.又称补充词典
 * 
* @For WI Cloud
 * 
 */
public class UserDefineRecognition {

	private Term[] terms = null;

	private WoodInterface[] forests = { UserDefineLibrary.FOREST };

	private int offe = -1;
	private int endOffe = -1;
	private int tempFreq = 50;
	private String tempNature;

	private WoodInterface branch = null;
	private WoodInterface forest = null;

	public UserDefineRecognition(Term[] terms, Forest... forests) {
		this.terms = terms;
		if (forests != null && forests.length > 0) {
			this.forests = forests;
		}

	}

	public void recognition() {

		for (WoodInterface forest : forests) {
			if (forest == null) {
				continue;
			}
			reset();
			this.forest = forest;

			branch = forest;

			int length = terms.length - 1;

			boolean flag = true;
			for (int i = 0; i < length; i++) {
				if (terms[i] == null)
					continue;
				if (branch == forest) {
					flag = false;
				} else {
					flag = true;
				}

				branch = termStatus(branch, terms[i]);
				if (branch == null) {
					if (offe != -1) {
						i = offe;
					}
					reset();
				} else if (branch.getStatus() == 3) {
					endOffe = i;
					tempNature = branch.getParams()[0];
					tempFreq = ObjectBean.getInt(branch.getParams()[1], 50);
					if (offe != -1 && offe < endOffe) {
						i = offe;
						makeNewTerm();
						reset();
					} else {
						reset();
					}
				} else if (branch.getStatus() == 2) {
					endOffe = i;
					if (offe == -1) {
						offe = i;
					} else {
						tempNature = branch.getParams()[0];
						tempFreq = ObjectBean.getInt(branch.getParams()[1], 50);
						if (flag) {
							makeNewTerm();
						}
					}
				} else if (branch.getStatus() == 1) {
					if (offe == -1) {
						offe = i;
					}
				}
			}
			if (offe != -1 && offe < endOffe) {
				makeNewTerm();
			}
		}
	}

	private void makeNewTerm() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		for (int j = offe; j <= endOffe; j++) {
			if (terms[j] == null) {
				continue;
			} else {
				sb.append(terms[j].getName());
			}
			// terms[j] = null;
		}
		TermNatures termNatures = new TermNatures(new TermNature(tempNature,
				tempFreq));
		Term term = new Term(sb.toString(), offe, termNatures);
		term.setNature(termNatures.termNatures[0].nature);
		term.selfScore = -1 * tempFreq;
		TermUtil.insertTerm(terms, term);
		// reset();
	}

	/**
	 * 重置
	 */
	private void reset() {
		offe = -1;
		endOffe = -1;
		tempFreq = 50;
		tempNature = null;
		branch = forest;
	}

	/**
	 * 传入一个term 返回这个term的状态
	 * 
	 * @param branch
	 * @param term
	 * @return
	 */
	private WoodInterface termStatus(WoodInterface branch, Term term) {
		String name = term.getName();
		for (int j = 0; j < name.length(); j++) {
			branch = branch.get(name.charAt(j));
			if (branch == null) {
				return null;
			}
		}
		return branch;
	}

}
