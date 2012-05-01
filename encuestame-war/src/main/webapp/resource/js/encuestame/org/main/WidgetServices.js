/*
 ************************************************************************************
 * Copyright (C) 2001-2011 encuestame: open source social survey Copyright (C) 2009
 * encuestame Development Team.
 * Licensed under the Apache Software License version 2.0
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to  in writing,  software  distributed
 * under the License is distributed  on  an  "AS IS"  BASIS,  WITHOUT  WARRANTIES  OR
 * CONDITIONS OF ANY KIND, either  express  or  implied.  See  the  License  for  the
 * specific language governing permissions and limitations under the License.
 ************************************************************************************
 */
dojo.provide("encuestame.org.main.WidgetServices");

dojo.require("encuestame.org.core.commons.dialog.ModalBox");

dojo.declare("encuestame.org.main.WidgetServices", null, {

    // constructor
    constructor: function(){},

    /*
     *
     */
    _delay_messages : 5000,

    /*
     *
     */
    _createModalBox : function(type, handler) {
        var modalBox = new encuestame.org.core.commons.dialog.ModalBox(dojo.byId("modal-box"), type, dojo.hitch(handler));
        return modalBox;
    },

    /*
     *
     */
    successMesage : function() {
        console.info("Successfull message");
        encuestame.messages.pubish(encuestame.constants.messageCodes["023"], "message", this._delay_messages);
    },

    /*
     *
     */
    warningMesage : function() {
        encuestame.messages.pubish(encuestame.constants.warningCodes["001"], "warning", this._delay_messages);
    },

    /*
     *
     */
    errorMesage : function(error) {
        var modal = this._createModalBox("alert", null);
        modal.show(error == null ? encuestame.constants.errorCodes["023"] : error);
    },

    /*
     *
     */
    infoMesage : function(info) {
         var modal = this._createModalBox("alert", null);
         modal.show(info);
    },

    /*
     *
     */
    fatalMesage : function() {
        encuestame.messages.pubish(encuestame.constants.errorCodes["023"], "fatal", this._delay_messages);
    },
    
    /**
     * 
     * @param params
     * @param load
     * @param service
     */
    callPOST : function(params, load, service, loadingFunction) {
        var error = dojo.hitch(this, function(errorMessage) {
        	 this.infoMesage(errorMessage);
        });
        encuestame.service.xhrPostParam(service, params, load, error, null, loadingFunction);
     },
     
     /**
      * 
      * @param e
      */
     stopEvent : function(e){
    	 dojo.stopEvent(e);
     }

});
