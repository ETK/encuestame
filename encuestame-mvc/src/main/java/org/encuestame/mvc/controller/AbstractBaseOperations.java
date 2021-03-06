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

package org.encuestame.mvc.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.encuestame.business.service.AbstractSurveyService;
import org.encuestame.business.service.DashboardService;
import org.encuestame.business.service.ProjectService;
import org.encuestame.business.service.ServiceManager;
import org.encuestame.business.service.TweetPollService;
import org.encuestame.core.config.EnMePlaceHolderConfigurer;
import org.encuestame.core.security.util.HTMLInputFilter;
import org.encuestame.core.service.AbstractSecurityContext;
import org.encuestame.core.service.SecurityService;
import org.encuestame.core.service.imp.GeoLocationSupport;
import org.encuestame.core.service.imp.ICommentService;
import org.encuestame.core.service.imp.IDashboardService;
import org.encuestame.core.service.imp.IFrontEndService;
import org.encuestame.core.service.imp.IPictureService;
import org.encuestame.core.service.imp.IPollService;
import org.encuestame.core.service.imp.IProjectService;
import org.encuestame.core.service.imp.IServiceManager;
import org.encuestame.core.service.imp.IStatisticsService;
import org.encuestame.core.service.imp.ISurveyService;
import org.encuestame.core.service.imp.ITweetPollService;
import org.encuestame.core.service.imp.SearchServiceOperations;
import org.encuestame.core.service.imp.SecurityOperations;
import org.encuestame.core.service.imp.StreamOperations;
import org.encuestame.core.util.ConvertDomainBean;
import org.encuestame.core.util.EnMeUtils;
import org.encuestame.persistence.domain.question.Question;
import org.encuestame.persistence.domain.security.UserAccount;
import org.encuestame.persistence.domain.tweetpoll.TweetPoll;
import org.encuestame.persistence.exception.EnMeExpcetion;
import org.encuestame.persistence.exception.EnMeNoResultsFoundException;
import org.encuestame.utils.DateUtil;
import org.encuestame.utils.RelativeTimeEnum;
import org.encuestame.utils.captcha.ReCaptcha;
import org.encuestame.utils.json.ProfileUserAccount;
import org.encuestame.utils.json.QuestionBean;
import org.encuestame.utils.json.TweetPollBean;
import org.encuestame.utils.net.InetAddresses;
import org.encuestame.utils.net.XFordwardedInetAddressUtil;
import org.encuestame.utils.web.HashTagBean;
import org.encuestame.utils.web.QuestionAnswerBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Base Controller.
 * @author Picado, Juan juanATencuestame.org
 * @since Mar 13, 2010 10:41:38 PM
 */
public abstract class AbstractBaseOperations extends AbstractSecurityContext{

     /**
      *
      */
     private Logger log = Logger.getLogger(this.getClass());

     /**
      * Simple date format.
      */
     public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DateUtil.DEFAULT_FORMAT_DATE);

     /**
      * Simple time format.
      */
     public static final SimpleDateFormat SIMPLE_TIME_FORMAT = new SimpleDateFormat(DateUtil.DEFAULT_FORMAT_TIME);

     /**
      *
      */
     protected static final Integer START_DEFAULT = 0;

     /**
      * Max total results to retrieve.
      */
     protected static final Integer MAX_RESULTS = 100;

     /**
      * {@link ReCaptcha}.
      */
     private ReCaptcha reCaptcha;

    /**
     * {@link ServiceManager}.
     */
    @Autowired
    private IServiceManager serviceManager;

    /** Force Proxy Pass Enabled. **/
    @Value("${application.proxyPass}") private Boolean proxyPass;

    /**
     * {@link AuthenticationManager}.
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * @return the serviceManager
     */
    public IServiceManager getServiceManager() {
        return serviceManager;
    }

    /**
     * Get Current Request Attributes.
     * @return {@link RequestAttributes}
     */
    public RequestAttributes getContexHolder(){
         return RequestContextHolder.currentRequestAttributes();
    }

    /**
     * Get {@link ServletRequestAttributes}.
     * @return {@link ServletRequestAttributes}
     */
    public HttpServletRequest getServletRequestAttributes(){
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    /**
     * Get By Username.
     * @param username username
     * @return
     * @throws EnMeNoResultsFoundException
     */
    public UserAccount getByUsername(final String username) throws EnMeNoResultsFoundException{
        return getServiceManager().getApplicationServices().getSecurityService().getUserAccount(username);
    }

    /**
     * Get by username without Exceptions.
     * @param username user name
     * @return {@link UserAccount}.
     */
    public UserAccount findByUsername(final String username){
        return getServiceManager().getApplicationServices().getSecurityService().findUserByUserName(username);
    }

    /**
     * Fetch user account currently logged.
     * @return
     * @throws EnMeNoResultsFoundException
     */
    public UserAccount getUserAccount() throws EnMeNoResultsFoundException{
        final UserAccount account = this.getByUsername(this.getUserPrincipalUsername());
        if (account == null) {
            log.fatal("user session not found ");
            throw new EnMeNoResultsFoundException("user not found");
        }
        return account;
    }

    /**
     * Create new tweetPoll.
     * @param question
     * @param hashtags
     * @param answers
     * @param user
     * @return
     * @throws EnMeExpcetion
     */
    //@Deprecated
    public TweetPoll createTweetPoll(
            final String question,
            String[] hashtags,
            UserAccount user) throws EnMeExpcetion{
        //create new tweetPoll
        final TweetPollBean tweetPollBean = new TweetPollBean();
        tweetPollBean.getHashTags().addAll(fillListOfHashTagsBean(hashtags));
        // save create tweet poll
        tweetPollBean.setUserId(user.getAccount().getUid());
        tweetPollBean.setCloseNotification(Boolean.FALSE);
        tweetPollBean.setResultNotification(Boolean.FALSE);
        //tweetPollBean.setPublishPoll(Boolean.TRUE); // always TRUE
        tweetPollBean.setSchedule(Boolean.FALSE);
        return getTweetPollService().createTweetPoll(tweetPollBean, question, user);
    }

    /**
     * Create {@link HashTagBean} list.
     * @param arrayHashTags
     * @return
     */
    public List<HashTagBean> createHashTagBeansList(final String[] arrayHashTags) {
    	final List<HashTagBean> tagBeanlist = new ArrayList<HashTagBean>();
    	for (int i = 0; i < arrayHashTags.length; i++) { 
			final HashTagBean itemTagBean = new HashTagBean();
			itemTagBean.setHashTagName(arrayHashTags[i]);
			tagBeanlist.add(itemTagBean);
		}	     	
    	return tagBeanlist;
    }
    
    /**
     *
     * @param tweetPollBean
     * @return
     * @throws EnMeExpcetion
     */
    public TweetPoll createTweetPoll(final TweetPollBean tweetPollBean) throws EnMeExpcetion{
        //create new tweetPoll
        log.debug("createTweetPoll Bean "+tweetPollBean.toString());
        return getTweetPollService().createTweetPoll(tweetPollBean,
                tweetPollBean.getQuestionBean().getQuestionName(),
                getUserAccount());
    }

    /**
     *
     * @param tweetPollBean
     * @return
     * @throws EnMeExpcetion
     */
    public TweetPoll updateTweetPoll(
            final TweetPollBean tweetPollBean) throws EnMeExpcetion{
            //final List<HashTagBean> hashtagsList = fillListOfHashTagsBean(hashtags);
            return getTweetPollService().updateTweetPoll(tweetPollBean);
       }

    /**
     * Get the Host IP Address.
     * @param 
     * @return ip as string format.
     */
    public String getIpClient(final HttpServletRequest request) {
        return EnMeUtils.getIP(request, this.proxyPass);
    }

    /**
     *
     * @param max
     * @return
     */
    public Integer limitTotalMax(Integer max) {
        return max == null ? null : (max > this.MAX_RESULTS ? this.MAX_RESULTS : max);
    }

    /**
     * Relative Time.
     * @param date
     * @return
     */
    @Deprecated
    protected  HashMap<Integer, RelativeTimeEnum> getRelativeTime(final Date date){
         return DateUtil.getRelativeTime(date);
    }


    /**
     *
     * @param tpbean
     * @param request
     */
    @Deprecated
    public void convertRelativeTime(final TweetPollBean tpbean, final HttpServletRequest request){
        final HashMap<Integer, RelativeTimeEnum> relativeTime = getRelativeTime(tpbean.getCreatedDateAt());
        final Iterator it = relativeTime.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry<Integer, RelativeTimeEnum> e = (Map.Entry<Integer, RelativeTimeEnum>)it.next();
            log.debug("--"+e.getKey() + "**" + e.getValue());
            tpbean.setRelativeTime(convertRelativeTimeMessage(e.getValue(), e.getKey(), request));
        }
    }

   /**
    * Convert Relative Time Message.
    * @param relativeTimeEnum
    * @param number
    * @param request
    * @param objects
    * @return
    */
    @Deprecated
   public String convertRelativeTimeMessage(final RelativeTimeEnum relativeTimeEnum, final Integer number,
           final HttpServletRequest request){
       final StringBuilder builder = new StringBuilder();
       //builder.append(number);
       //builder.append(" ");
       log.debug("Convert Message Relative Time");
       log.debug(relativeTimeEnum);
       log.debug(number);
       String str[] = {number.toString()};
       if (relativeTimeEnum.equals(RelativeTimeEnum.ONE_SECOND_AGO)) {
           builder.append(getMessage("relative.time.one.second.ago", request, str));
       } else if(relativeTimeEnum.equals(RelativeTimeEnum.SECONDS_AGO)) {
           builder.append(getMessage("relative.time.one.seconds.ago", request, str));
       } else if(relativeTimeEnum.equals(RelativeTimeEnum.A_MINUTE_AGO)) {
           builder.append(getMessage("relative.time.one.minute.ago", request, str));
       } else if(relativeTimeEnum.equals(RelativeTimeEnum.MINUTES_AGO)) {
           builder.append(getMessage("relative.time.one.minutes.ago", request, str));
       } else if(relativeTimeEnum.equals(RelativeTimeEnum.AN_HOUR_AGO)) {
           builder.append(getMessage("relative.time.one.hour.ago", request, str));
       } else if(relativeTimeEnum.equals(RelativeTimeEnum.HOURS_AGO)) {
           builder.append(getMessage("relative.time.one.hours.ago", request, str));
       } else if(relativeTimeEnum.equals(RelativeTimeEnum.MONTHS_AGO)) {
           builder.append(getMessage("relative.time.one.months.ago", request, str));
       } else if(relativeTimeEnum.equals(RelativeTimeEnum.ONE_MONTH_AGO)) {
           builder.append(getMessage("relative.time.one.month.ago", request, str));
       } else if(relativeTimeEnum.equals(RelativeTimeEnum.ONE_YEAR_AGO)) {
           builder.append(getMessage("relative.time.one.year.ago", request, str));
       } else if(relativeTimeEnum.equals(RelativeTimeEnum.YEARS_AGO)) {
           builder.append(getMessage("relative.time.one.years.ago", request, str));
       }
       return builder.toString();
   }



    /**
     * Get Message with Locale.
     * @param message
     * @param request
     * @param args
     * @return
     */

    public String getMessage(final String message,
            final HttpServletRequest request, Object[] args) {
        String stringValue = "";
        try {
            stringValue = getServiceManager().getMessageSource().getMessage(
                    message, args, getLocale(request));
        } catch (Exception e) {
            log.error(e);  //TODO: ENCUESTAME-223 - OPEN
        }
        return stringValue;
    }

    /**
     * Get Message.
     * @param message
     * @param args
     * @return
     */
    //@Deprecated
    public String getMessage(final String message, Object[] args){
        return getMessage(message, null, args);
    }

    /**
     * Get Message.
     * @param message
     * @return
     */
    //@Deprecated
    public String getMessage(final String message){
        return getMessage(message, null, null);
    }

    /**
     * Get Locale.
     * @param request
     * @return
     */
    private Locale getLocale(final HttpServletRequest request) {
        if(request == null){
            return Locale.ENGLISH;
        } else {
            return RequestContextUtils.getLocale(request);
        }
    }

    /**
     * Filter Value.
     * @param value value.
     * @return
     */
    public String filterValue(String value){
        final HTMLInputFilter vFilter = new HTMLInputFilter(true);
        return vFilter.filter(value);
    }

    /**
     * @param serviceManager
     *            the serviceManager to set
     */
    public void setServiceManager(IServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    /**
     * Get {@link AbstractSurveyService}.
     * @return survey service
     */
    public ISurveyService getSurveyService(){
        return getServiceManager().getApplicationServices().getSurveyService();
    }

    /**
     * Get {@link DashboardService}
     * @return
     */
    public IDashboardService getDashboardService(){
        return getServiceManager().getApplicationServices().getDashboardService();
    }

    /**
     *
     * @return
     */
    public ICommentService getCommentService(){
        return getServiceManager().getApplicationServices().getCommentService();
    }

    /**
     *
     * @return
     */
    public StreamOperations getStreamOperations(){
        return getServiceManager().getApplicationServices().getStreamOperations();
    }

    /**
     * Get {@link SecurityService}.
     * @return
     */
    public SecurityOperations getSecurityService(){
        return getServiceManager().getApplicationServices().getSecurityService();
    }

    /**
     * Get {@link SearchServiceOperations}.
     * @return
     */
    public SearchServiceOperations getSearchService(){
        return getServiceManager().getApplicationServices().getSearchService();
    }

    /**
     * Location Service.
     * @return
     */
    public GeoLocationSupport getLocationService(){
        return getServiceManager().getApplicationServices().getLocationService();
    }

    /**
     * Get {@link TweetPollService}.
     * @return
     */
    public ITweetPollService getTweetPollService(){
        return getServiceManager().getApplicationServices().getTweetPollService();
    }

    public IPollService getPollService(){
        return getServiceManager().getApplicationServices().getPollService();
    }

    /**
     * Get {@link ProjectService}.
     * @return
     */
    public IProjectService getProjectService(){
        return getServiceManager().getApplicationServices().getProjectService();
    }

    /**
     * Get {@link FrontEndCoreService}.
     * @return
     */
    public IFrontEndService getFrontService(){
        return getServiceManager().getApplicationServices().getFrontEndService();
    }
    
    /**
     * 
     * @return
     */
    public IStatisticsService getStatisticsService(){
        return getServiceManager().getApplicationServices().getStatisticService();
    }

    /**
     * Get Picture Service.
     * @return
     */
    public IPictureService getPictureService(){
        return getServiceManager().getApplicationServices().getPictureService();
    }

    /**
     * @return the authenticationManager
     */
    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    /**
     * @param authenticationManager the authenticationManager to set
     */
    public void setAuthenticationManager(final AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }


    /**
     * @return the reCaptcha
     */
    public ReCaptcha getReCaptcha() {
        return reCaptcha;
    }

    /**
     * @param reCaptcha
     *            the reCaptcha to set
     */
    @Autowired
    public void setReCaptcha(final ReCaptcha reCaptcha) {
        this.reCaptcha = reCaptcha;
    }

    /**
     * Get Format Date.
     * @param date
     * @return
     */
    public Date getFormatDate(final String date){
        Assert.assertNotNull(date);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.DEFAULT_FORMAT_DATE);
        simpleDateFormat.format(DateUtil.DEFAULT_FORMAT_DATE);
        return simpleDateFormat.getCalendar().getTime();
    }

    /**
     * Get full profile logged user info.
     * @return
     * @throws EnMeNoResultsFoundException
     */
    public ProfileUserAccount getProfileUserInfo() throws EnMeNoResultsFoundException{
        return ConvertDomainBean.convertUserAccountToUserProfileBean(getUserAccount());
    }

    /**
     * Create question with answers.
     * @param questionName question description
     * @param user {@link UserAccount} owner.
     * @return {@link Question}
     * @throws EnMeExpcetion exception
     */
    public Question createQuestion(final String questionName, final String[] answers, final UserAccount user) throws EnMeExpcetion{
        final QuestionBean questionBean = new QuestionBean();
        questionBean.setQuestionName(questionName);
        questionBean.setUserId(user.getUid());
        // setting Answers.
        for (int row = 0; row < answers.length; row++) {
            final QuestionAnswerBean answer = new QuestionAnswerBean();
            answer.setAnswers(answers[row].trim());
            answer.setAnswerHash(RandomStringUtils.randomAscii(5));
            questionBean.getListAnswers().add(answer);
        }
        final Question questionDomain = getSurveyService().createQuestion(
                questionBean);
        return questionDomain;
    }

    /**
     * Create a list of {@link HashTagBean}.
     * @param hashtags array of hashtags strings.
     * @return list of {@link HashTagBean}.
     */
    public List<HashTagBean> fillListOfHashTagsBean(String[] hashtags) {
        final List<HashTagBean> hashtagsList = new ArrayList<HashTagBean>();
        hashtags = hashtags == null ? new String[0] : hashtags;
        log.debug("HashTag size:{" + hashtags.length);
        for (int row = 0; row < hashtags.length; row++) {
            final HashTagBean hashTag = new HashTagBean();
            final String hashtagE = hashtags[row];
            if (hashtagE != null && !hashtagE.isEmpty()) {
                log.debug("HashTag:{" + hashTag);
                hashTag.setHashTagName(hashtags[row].toLowerCase().trim());
                hashtagsList.add(hashTag);
            } else {
                log.warn("Trying to save empty or null hashtag?");
            }
        }
        log.debug("hashtag fillListOfHashTagsBean->"+hashtagsList.size());
        return hashtagsList;
    }
    
    /**
     * Add to the model the i18n message property.
     * @param model {@link Model}
     * @param key layout custom key
     * @param value message key
     */
    public void addi18nProperty(final ModelMap model, final String key, final String value) {
    	@SuppressWarnings("unchecked")
		HashMap<String, String> i18n = (HashMap<String, String>) model.get("i18n");
    	if (i18n == null) {
    		i18n = new HashMap<String, String>();
    		model.addAttribute("i18n", i18n);
    	}
    	i18n.put(key, value);
    }

    /**
     * Add to the model the i18n message property, the layout custom key will
     * be the same of the message key.
     * @param model
     * @param key
     */
    public void addi18nProperty(final ModelMap model, final String key) {
    	@SuppressWarnings("unchecked")
		HashMap<String, String> i18n = (HashMap<String, String>) model.get("i18n");
    	if (i18n == null) {
    		i18n = new HashMap<String, String>();
    		model.addAttribute("i18n", i18n);
    	}
    	i18n.put(key, getMessage(key));
    }

   /**
    * If is not complete check and validate current status.
    * @param tweetPoll
    */
   public void checkTweetPollStatus(final TweetPoll tweetPoll){
       if (!tweetPoll.getCompleted()) {
           getTweetPollService().checkTweetPollCompleteStatus(tweetPoll);
       }
   }

   /**
    *
    * @return
    */
   public Boolean isSocialSignInUpEnabled(){
       return EnMePlaceHolderConfigurer.getBooleanProperty("application.social.signin.enabled");
   }
   
   /**
    * Add to model defaults messages.
    * TODO: move to INTERCEPTOR.
    */
   public final void addDefaulti18nMessages(final ModelMap model) {
	   addi18nProperty(model, "profile_menu_configuration", getMessage("profile_menu_configuration"));
	   addi18nProperty(model, "profile_menu_social", getMessage("profile_menu_social"));
	   addi18nProperty(model, "profile_menu_help", getMessage("profile_menu_help"));
	   addi18nProperty(model, "profile_menu_log_out", getMessage("profile_menu_log_out"));
   }
   
   /**
    * Add to model the social picker messages.
    * @param model
    */
   public final void addSocialPickerWidgetMessages(final ModelMap model) {
	   addi18nProperty(model, "social_picker_only_selected", getMessage("social_picker_only_selected"));
	   addi18nProperty(model, "social_picker_select_all", getMessage("social_picker_select_all"));
	   addi18nProperty(model, "social_picker_unselect_all", getMessage("social_picker_unselect_all"));
	   addi18nProperty(model, "social_picker_accounts_selected", getMessage("social_picker_accounts_selected"));
	   addi18nProperty(model, "social_picker_filter_selected", getMessage("social_picker_filter_selected"));	   
	   addi18nProperty(model, "e_022", getMessage("e_022"));
   }   
   
   /**
    * 
    * @param model
    */
   public final void addItemsManangeMessages(final ModelMap model) {
	addi18nProperty(model, "detail_manage_by_account", getMessage("detail_manage_by_account"));
   	addi18nProperty(model, "detail_manage_today", getMessage("detail_manage_today"));
   	addi18nProperty(model, "detail_manage_last_week", getMessage("detail_manage_last_week"));
   	addi18nProperty(model, "detail_manage_favorites", getMessage("detail_manage_favorites"));
   	addi18nProperty(model, "detail_manage_scheduled", getMessage("detail_manage_scheduled"));
   	addi18nProperty(model, "detail_manage_all", getMessage("detail_manage_all"));
   	addi18nProperty(model, "detail_manage_published", getMessage("detail_manage_published"));
   	addi18nProperty(model, "detail_manage_unpublished", getMessage("detail_manage_unpublished"));
   	addi18nProperty(model, "detail_manage_only_completed", getMessage("detail_manage_only_completed"));
   	//folder messages
   	addi18nProperty(model, "detail_manage_folder_title", getMessage("detail_manage_folder_title"));
   	addi18nProperty(model, "detail_manage_delete", getMessage("detail_manage_delete"));
   	addi18nProperty(model, "detail_manage_new", getMessage("detail_manage_new"));
   	addi18nProperty(model, "detail_manage_search", getMessage("detail_manage_search"));
   	addi18nProperty(model, "detail_manage_folder_replace_name", getMessage("detail_manage_folder_replace_name"));
   	//filters
   	addi18nProperty(model, "detail_manage_filters_advanced", getMessage("detail_manage_filters_advanced"));
   	addi18nProperty(model, "detail_manage_filters_order", getMessage("detail_manage_filters_order"));
   	addi18nProperty(model, "detail_manage_filters_social_network", getMessage("detail_manage_filters_social_network"));
   	addi18nProperty(model, "detail_manage_filters_votes_options", getMessage("detail_manage_filters_votes_options"));
   	// advanced filter
   	addi18nProperty(model, "detail_manage_filters_advanced_title", getMessage("detail_manage_filters_advanced_title"));
   	addi18nProperty(model, "detail_manage_filters_advanced_type_to_search", getMessage("detail_manage_filters_advanced_type_to_search"));
   	addi18nProperty(model, "detail_manage_filters_advanced_all_results", getMessage("detail_manage_filters_advanced_all_results"));
   	addi18nProperty(model, "detail_manage_filters_advanced_range_days", getMessage("detail_manage_filters_advanced_range_days"));
   	
   	addi18nProperty(model, "commons_filter", getMessage("commons_filter"));
   	addSocialPickerWidgetMessages(model);
   }
}
