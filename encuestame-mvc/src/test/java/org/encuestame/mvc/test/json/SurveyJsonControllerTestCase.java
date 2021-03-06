/*
 ************************************************************************************
 * Copyright (C) 2001-2011 encuestame: system online surveys Copyright (C) 2011
 * encuestame Development Team.
 * Licensed under the Apache Software License version 2.0
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to  in writing,  software  distributed
 * under the License is distributed  on  an  "AS IS"  BASIS,  WITHOUT  WARRANTIES  OR
 * CONDITIONS OF ANY KIND, either  express  or  implied.  See  the  License  for  the
 * specific language governing permissions and limitations under the License.
 ************************************************************************************
 */
package org.encuestame.mvc.test.json;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;

import junit.framework.Assert;

import org.encuestame.mvc.controller.json.survey.SurveyJsonController;
import org.encuestame.mvc.test.config.AbstractJsonMvcUnitBeans;
import org.encuestame.persistence.domain.security.UserAccount;
import org.encuestame.persistence.domain.survey.Survey;
import org.encuestame.persistence.domain.survey.SurveyFormat;
import org.encuestame.utils.categories.test.DefaultTest;
import org.encuestame.utils.enums.MethodJson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Test for {@link SurveyJsonController}.
 * @author Morales, Diana Paola paolaATencuestame.org
 * @since October 24, 2011
 */
@Category(DefaultTest.class)
public class SurveyJsonControllerTestCase extends AbstractJsonMvcUnitBeans {

    /** {@link UserAccount} **/
    private UserAccount userAccount;

    /** {@link Survey} **/
    private Survey survey;

    /** {@link SurveyFormat} **/
    private SurveyFormat format;

     /**
     * Init.
     */
    @Before
    public void initJsonService(){
        this.userAccount = getSpringSecurityLoggedUserAccount();
        final Date myDate = new Date();
        this.format = createSurveyFormat("My TextBox", new Date());
        this.survey = createSurvey(null, new Date(), new Date(), userAccount.getAccount(), myDate, format, "My Survey", myDate );
        this.survey.setFavorites(Boolean.TRUE);
        this.survey.setEditorOwner(userAccount);
        getAccountDao().saveOrUpdate(this.survey);

    }

    /**
     * Test /api/survey/search.json.
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void testSurveyJsonService() throws ServletException, IOException{
        Assert.assertNotNull(survey);
        // Search All surveys.
        initService("/api/survey/search.json", MethodJson.GET);
        setParameter("typeSearch", "ALL");
        setParameter("keyword", "My");
        setParameter("max", "100");
        setParameter("start", "0");
        final JSONObject response = callJsonService();
        final JSONObject sucess = getSucess(response);
        Assert.assertNotNull(sucess.get("surveys"));
        final JSONArray array = (JSONArray) sucess.get("surveys");
        Assert.assertEquals(array.size(), 1);

      //search by keyword
        initService("/api/survey/search.json", MethodJson.GET);
        setParameter("typeSearch", "BYOWNER");
        setParameter("keyword", "My");
        setParameter("max", "100");
        setParameter("start", "0");
        final JSONObject response2 = callJsonService();
        final JSONObject sucess2 = getSucess(response2);
        Assert.assertNotNull(sucess2.get("surveys"));
        final JSONArray array2 = (JSONArray) sucess2.get("surveys");
        Assert.assertEquals(array2.size(), 1);

         //LASTDAY
        initService("/api/survey/search.json", MethodJson.GET);
        setParameter("typeSearch", "LASTDAY");
        setParameter("max", "100");
        setParameter("start", "0");
        final JSONObject response3 = callJsonService();
        final JSONObject sucess3 = getSucess(response3);
        Assert.assertNotNull(sucess3.get("surveys"));
        final JSONArray array3 = (JSONArray) sucess3.get("surveys");
        Assert.assertEquals(array3.size(), 1);

       //FAVOURITES
        initService("/api/survey/search.json", MethodJson.GET);
        setParameter("typeSearch", "FAVOURITES");
        setParameter("max", "100");
        setParameter("start", "0");
        final JSONObject response4= callJsonService();
        final JSONObject sucess4 = getSucess(response4);
        Assert.assertNotNull(sucess4.get("surveys"));
        final JSONArray array4 = (JSONArray) sucess4.get("surveys");
        Assert.assertEquals(array4.size(), 1);

         //LASTWEEK
        final Calendar lastWeek = Calendar.getInstance();
        lastWeek.add(Calendar.DATE, -8);
        final Survey lastweetkSurvey = createSurvey(null, new Date(),
                new Date(), userAccount.getAccount(), lastWeek.getTime(),
                this.format, "My Survey", lastWeek.getTime());

        lastweetkSurvey.setEditorOwner(userAccount);
        getSurveyDaoImp().saveOrUpdate(lastweetkSurvey);

        initService("/api/survey/search.json", MethodJson.GET);
        setParameter("typeSearch", "LASTWEEK");
        setParameter("max", "100");
        setParameter("start", "0");
        final JSONObject response5= callJsonService();
        final JSONObject sucess5 = getSucess(response5);
        Assert.assertNotNull(sucess5.get("surveys"));
        final JSONArray array5 = (JSONArray) sucess5.get("surveys");
        Assert.assertEquals(array5.size(), 1);

        // ELSE
        initService("/api/survey/search.json", MethodJson.GET);
        setParameter("typeSearch", "");
        setParameter("max", "100");
        setParameter("start", "0");
        final JSONObject response7= callJsonService();
        final JSONObject sucess7 = getSucess(response7);
        Assert.assertNotNull(sucess7.get("surveys"));
        final JSONArray array7 = (JSONArray) sucess7.get("surveys");
        Assert.assertEquals(array7.size(), 2);
    }

}
