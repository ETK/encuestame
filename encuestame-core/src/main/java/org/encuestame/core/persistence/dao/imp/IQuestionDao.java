/*
 ************************************************************************************
 * Copyright (C) 2001-2009 encuestame: system online surveys Copyright (C) 2009
 * encuestame Development Team.
 * Licensed under the Apache Software License version 2.0
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to  in writing,  software  distributed
 * under the License is distributed  on  an  "AS IS"  BASIS,  WITHOUT  WARRANTIES  OR
 * CONDITIONS OF ANY KIND, either  express  or  implied.  See  the  License  for  the
 * specific language governing permissions and limitations under the License.
 ************************************************************************************
 */
package org.encuestame.core.persistence.dao.imp;

import java.util.List;

import org.encuestame.core.persistence.pojo.Questions;
import org.encuestame.core.persistence.pojo.QuestionPattern;
import org.encuestame.core.persistence.pojo.QuestionsAnswers;
import org.hibernate.HibernateException;

 /**
  * Interface to implement Question Dao.
  * @author Picado, Juan juan@encuestame.org
  * @since  02/06/2009 19:44:21
  * @version $Id$
  */
public interface IQuestionDao extends IBaseDao {

    /**
     * Create Question.
     * @param question question
     * @throws HibernateException exception
     */
    void createQuestion(final Questions question);

    /**
     * Load All Questions.
     * @return List of {@link Questions}
     * @throws HibernateException exception
     */
    public List<Questions> loadAllQuestions();

    /**
     * Load All Questions Patron.
     * @return  List of {@link QuestionPattern}
     * @throws HibernateException exception
     */
    List<QuestionPattern> loadAllQuestionPattern()
    throws HibernateException;

    /**
     * Load pattern info.
     * @param patronId patron id
     * @return QuestionPatron
     * @throws HibernateException exception
     */
    QuestionPattern loadPatternInfo(final Long patronId);

    /**
     * Retrieve Questions by Name.
     * @param keyword keyword
     * @return list of questions
     */
    List<Questions> retrieveQuestionsByName(final String keyword);

    /**
     * Retrieve Question By Id.
     * @param questionId question id
     * @return  {@link Questions}
     */
    Questions retrieveQuestionById(final Long questionId);

    /**
     * Get Questions Answer By Question Id.
     * @param questionId question id
     * @return list of answers
     * @throws HibernateException exception
     */
    List<QuestionsAnswers> getAnswersByQuestionId(final Long questionId);

    /**
     * Retrieve Answer by Id.
     * @param answerId answer id
     * @return {@link QuestionsAnswers}
     */
    QuestionsAnswers retrieveAnswerById(final Long answerId);
}