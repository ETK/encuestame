/*
 ************************************************************************************
 * Copyright (C) 2001-2010 encuestame: system online surveys Copyright (C) 2010
 * encuestame Development Team.
 * Licensed under the Apache Software License version 2.0
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to  in writing,  software  distributed
 * under the License is distributed  on  an  "AS IS"  BASIS,  WITHOUT  WARRANTIES  OR
 * CONDITIONS OF ANY KIND, either  express  or  implied.  See  the  License  for  the
 * specific language governing permissions and limitations under the License.
 ************************************************************************************
 */

package org.encuestame.core.service;

import org.apache.commons.lang.RandomStringUtils;
import org.encuestame.core.exception.EnMeExpcetion;
import org.encuestame.core.persistence.pojo.Poll;
import org.encuestame.core.persistence.pojo.Questions;
import org.encuestame.core.persistence.pojo.QuestionsAnswers;
import org.encuestame.core.service.util.MD5Utils;
import org.encuestame.utils.web.UnitAnswersBean;
import org.encuestame.utils.web.UnitPoll;
import org.encuestame.utils.web.UnitQuestionBean;
import org.springframework.stereotype.Service;

/**
 * Poll Service.
 * @author Morales, Diana Paola paola AT encuestame.org
 * @since  April 01, 2010
 * @version $Id: $
 */
@Service
public class PollService extends AbstractSurveyService implements IPollService{

    public final void createPoll(final UnitPoll pollBean ){
        final Poll pollDomain = new Poll();
        final Questions question = getQuestionDao().retrieveQuestionById(pollBean.getQuestionBean().getId());
        pollDomain.setCreatedAt(pollBean.getCreationDate());

       //  pollDomain.setPollCompleted(pollCompleted);
        try {
            pollDomain.setPollHash(MD5Utils.md5(RandomStringUtils.randomAlphanumeric(500)));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
         pollDomain.setQuestion(question);
         this.getPollDao().saveOrUpdate(pollDomain);
    }

//FIXME: Reutilize method
    /**
     * Save Question Answer.
     * @param answerBean answer
     * @param question question
     */
    public void saveAnswer(final UnitAnswersBean answerBean, final Questions question){
            final QuestionsAnswers answer = new QuestionsAnswers();
            answer.setQuestions(question);
            answer.setAnswer(answerBean.getAnswers());
            answer.setUniqueAnserHash(answerBean.getAnswerHash());
            this.getQuestionDao().saveOrUpdate(answer);
            answerBean.setAnswerId(answer.getQuestionAnswerId());
    }

    //FIXME: Reutilize method
    /**
     * Create Question.
     * @param questionBean {@link UnitQuestionBean}.
     * @throws EnMeExpcetion exception
     */
    public void createQuestion(final UnitQuestionBean questionBean) throws EnMeExpcetion{
            try{
                final Questions question = new Questions();
                question.setQuestion(questionBean.getQuestionName());
               // question.setSecUsersQuestion(getClientDao().getClientById((questionBean.getUserId()));
                question.setQidKey(MD5Utils.md5(RandomStringUtils.randomAlphanumeric(500)));
                question.setSharedQuestion(false);
                getQuestionDao().saveOrUpdate(question);
                questionBean.setId(question.getQid());
                for (final UnitAnswersBean answerBean : questionBean.getListAnswers()) {
                    this.saveAnswer(answerBean, question);
                }
            }
            catch (Exception e) {
                throw new EnMeExpcetion(e);
            }
    }

}