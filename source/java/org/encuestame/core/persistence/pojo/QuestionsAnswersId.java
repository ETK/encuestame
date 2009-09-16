package org.encuestame.core.persistence.pojo;

// Generated 29-may-2009 13:17:50 by Hibernate Tools 3.2.2.GA

/**
 * QuestionsAnswersId generated by hbm2java
 */
public class QuestionsAnswersId implements java.io.Serializable {

	private int idAnswers;
	private int qid;

	public QuestionsAnswersId() {
	}

	public QuestionsAnswersId(int idAnswers, int qid) {
		this.idAnswers = idAnswers;
		this.qid = qid;
	}

	public int getIdAnswers() {
		return this.idAnswers;
	}

	public void setIdAnswers(int idAnswers) {
		this.idAnswers = idAnswers;
	}

	public int getQid() {
		return this.qid;
	}

	public void setQid(int qid) {
		this.qid = qid;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof QuestionsAnswersId))
			return false;
		QuestionsAnswersId castOther = (QuestionsAnswersId) other;

		return (this.getIdAnswers() == castOther.getIdAnswers())
				&& (this.getQid() == castOther.getQid());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getIdAnswers();
		result = 37 * result + this.getQid();
		return result;
	}

}