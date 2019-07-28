package com.instabug.reactlibrary;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.JavaOnlyArray;
import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.instabug.bug.BugReporting;
import com.instabug.bug.OnSdkDismissedCallback;
import com.instabug.chat.Chats;
import com.instabug.chat.Replies;
import com.instabug.crash.CrashReporting;
import com.instabug.featuresrequest.ActionType;
import com.instabug.featuresrequest.FeatureRequests;
import com.instabug.library.Feature;
import com.instabug.library.OnSdkDismissCallback;
import com.instabug.library.extendedbugreport.ExtendedBugReport;
import com.instabug.library.invocation.InstabugInvocationEvent;
import com.instabug.library.invocation.OnInvokeCallback;
import com.instabug.library.invocation.util.InstabugVideoRecordingButtonPosition;
import com.instabug.library.model.Report;
import com.instabug.reactlibrary.utils.ArrayUtil;
import com.instabug.survey.OnDismissCallback;
import com.instabug.survey.OnShowCallback;
import com.instabug.survey.Surveys;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static com.instabug.reactlibrary.utils.InstabugUtil.getMethod;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Looper.class, android.os.Handler.class, BugReporting.class, CrashReporting.class, FeatureRequests.class, Chats.class, Replies.class, SystemClock.class, Surveys.class, Runnable.class, WritableNativeArray.class, JSONObject.class, RNInstabugReactnativeModule.class, Arguments.class, ArrayUtil.class})

public class RNInstabugReactnativeModuleTest {

    private RNInstabugReactnativeModule rnModule = new RNInstabugReactnativeModule(null,null,null);

    private final static ScheduledExecutorService mainThread = Executors.newSingleThreadScheduledExecutor();

    @Before
    public void mockMainThreadHandler() throws Exception {
        PowerMockito.mockStatic(Looper.class);
        Looper mockMainThreadLooper = mock(Looper.class);
        when(Looper.getMainLooper()).thenReturn(mockMainThreadLooper);
        Handler mockMainThreadHandler = mock(Handler.class);
        Answer<Boolean> handlerPostAnswer = new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                invocation.getArgumentAt(0, Runnable.class).run();
                return true;
            }
        };
        doAnswer(handlerPostAnswer).when(mockMainThreadHandler).post(any(Runnable.class));
        doAnswer(handlerPostAnswer).when(mockMainThreadHandler).postDelayed(any(Runnable.class), anyLong());
        PowerMockito.whenNew(Handler.class).withArguments(mockMainThreadLooper).thenReturn(mockMainThreadHandler);
    }

    /********BugReporting*********/

    @Test
    public void given$invoke_whenQuery_thenShouldCallNativeApi() {
        // given
        PowerMockito.mockStatic(BugReporting.class);
        // when
        rnModule.invoke();
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        BugReporting.invoke();
    }

    @Test
    public void givenShakingThreshold$setShakingThresholdForAndroid_whenQuery_thenShouldCallNativeApiWithShakingThreshold() {
        // given
        PowerMockito.mockStatic(BugReporting.class);
        int shakingThreshold = 30;
        // when
        rnModule.setShakingThresholdForAndroid(shakingThreshold);
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        BugReporting.setShakingThreshold(shakingThreshold);
    }

    @Test
    public void givenFalse$setBugReportingEnabled_whenQuery_thenShouldCallNativeApiWithDisabled() {
        // given
        PowerMockito.mockStatic(BugReporting.class);
        // when
        rnModule.setBugReportingEnabled(false);
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        BugReporting.setState(Feature.State.DISABLED);
    }

    @Test
    public void givenTrue$setBugReportingEnabled_whenQuery_thenShouldCallNativeApiWithEnabled() {
        // given
        PowerMockito.mockStatic(BugReporting.class);
        // when
        rnModule.setBugReportingEnabled(true);
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        BugReporting.setState(Feature.State.ENABLED);
    }

    @Test
    public void givenIsEnabled$setAutoScreenRecordingEnabled_whenQuery_thenShouldCallNativeApiWithIsEnabled() {
        // given
        PowerMockito.mockStatic(BugReporting.class);
        // when
        rnModule.setAutoScreenRecordingEnabled(true);
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        BugReporting.setAutoScreenRecordingEnabled(true);
    }

    @Test
    public void givenTrue$setViewHierarchyEnabled_whenQuery_shouldCallNativeApiWithEnabled() {
        // given
        PowerMockito.mockStatic(BugReporting.class);
        // when
        rnModule.setViewHierarchyEnabled(true);
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        BugReporting.setViewHierarchyState(Feature.State.ENABLED);
    }

    @Test
    public void givenFalse$setViewHierarchyEnabled_whenQuery_shouldCallNativeApiWithDisabled() {
        // given
        PowerMockito.mockStatic(BugReporting.class);
        // when
        rnModule.setViewHierarchyEnabled(false);
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        BugReporting.setViewHierarchyState(Feature.State.DISABLED);
    }

    @Test
    public void givenBooleanArgs$setEnabledAttachmentTypes_whenQuery_shouldCallNativeApiWithBooleanArgs() {
        // given
        PowerMockito.mockStatic(BugReporting.class);
        // when
        rnModule.setEnabledAttachmentTypes(true, true, false, true);
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        BugReporting.setAttachmentTypesEnabled(true, true, false, true);
    }

    @Test
    public void givenExtendedBugReportMode$setExtendedBugReportMode_whenQuery_thenShouldCallNativeApiWithArgs() {
        // given
        PowerMockito.mockStatic(BugReporting.class);
        Map<String, Object> args = new HashMap<>();
        ArgsRegistry.registerInstabugExtendedBugReportModeArgs(args);
        // when
        for (String strExtendedBugReportMode : args.keySet()) {
            rnModule.setExtendedBugReportMode(strExtendedBugReportMode);
        }
        // then
        for (Object extendedBugReportMode : args.values()) {
            ExtendedBugReport.State state = (ExtendedBugReport.State) extendedBugReportMode;
            PowerMockito.verifyStatic(VerificationModeFactory.times(1));
            BugReporting.setExtendedBugReportState(state);
        }
    }

    @Test
    public void givenInvocationEvent$setInvocationEvents_whenQuery_thenShouldCallNativeApiWithArgs() {
        // given
        PowerMockito.mockStatic(BugReporting.class);
        final Map<String, Object> args = new HashMap<>();
        ArgsRegistry.registerInstabugInvocationEventsArgs(args);
        final String[] keysArray = args.keySet().toArray(new String[0]);
        JavaOnlyArray actualArray = new JavaOnlyArray();
        for (String key : keysArray) {
            actualArray.pushString(key);
        }
        // when
        rnModule.setInvocationEvents(actualArray);
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        BugReporting.setInvocationEvents(args.values().toArray(new InstabugInvocationEvent[0]));
    }

    @Test
    public void givenOptions$setInvocationOptions_whenQuery_thenShouldCallNativeApiWithArgs() {
        // given
        PowerMockito.mockStatic(BugReporting.class);
        final Map<String, Object> args = new HashMap<>();
        ArgsRegistry.registerInvocationOptionsArgs(args);
        final String[] keysArray = args.keySet().toArray(new String[0]);
        JavaOnlyArray actualArray = new JavaOnlyArray();
        actualArray.pushString(keysArray[0]);
        actualArray.pushString(keysArray[1]);
        // when
        rnModule.setInvocationOptions(actualArray);
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        int option1 = (int) args.get(keysArray[0]);
        int option2 = (int) args.get(keysArray[1]);
        BugReporting.setOptions(option1, option2);
    }

    @Test
    public void given$setPreInvocationHandler_whenQuery_thenShouldSetNativeCallback() {

        try {
            // given
            PowerMockito.mockStatic(BugReporting.class);
            RNInstabugReactnativeModule mockModule = mock(RNInstabugReactnativeModule.class);
            PowerMockito.doCallRealMethod().when(mockModule, "setPreInvocationHandler", Matchers.any());
            PowerMockito.doAnswer(new Answer<Object>() {
                @Override
                public Object answer(InvocationOnMock invocation) {
                    ((OnInvokeCallback) invocation.getArguments()[0]).onInvoke();
                    return null;
                }
            }).when(BugReporting.class, "setOnInvokeCallback", Matchers.anyObject());
            // when
            mockModule.setPreInvocationHandler(null);
            // then
            verifyPrivate(mockModule, VerificationModeFactory.times(1))
                    .invoke("sendEvent", any(), eq("IBGpreInvocationHandler"), eq(null));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void given$setPostInvocationHandler_whenQuery_thenShouldSetNativeCallback() {
        try {
            // given
            PowerMockito.mockStatic(BugReporting.class);
            PowerMockito.mockStatic(Arguments.class);
            RNInstabugReactnativeModule mockModule = mock(RNInstabugReactnativeModule.class);
            PowerMockito.doCallRealMethod().when(mockModule, "setPostInvocationHandler", Matchers.any());
            PowerMockito.when(Arguments.createMap()).thenReturn(new JavaOnlyMap());
            PowerMockito.doAnswer(new Answer<Object>() {
                @Override
                public Object answer(InvocationOnMock invocation) {
                    ((OnSdkDismissCallback) invocation.getArguments()[0])
                            .call(OnSdkDismissCallback.DismissType.CANCEL, OnSdkDismissCallback.ReportType.BUG);
                    return null;
                }
            }).when(BugReporting.class, "setOnDismissCallback", Matchers.anyObject());
            // when
            mockModule.setPostInvocationHandler(null);
            // then
            WritableMap params = new JavaOnlyMap();
            params.putString("dismissType", OnSdkDismissedCallback.DismissType.CANCEL.toString());
            params.putString("reportType", OnSdkDismissCallback.ReportType.BUG.toString());
            verifyPrivate(mockModule, VerificationModeFactory.times(1))
                    .invoke("sendEvent", any(), eq("IBGpostInvocationHandler"), eq(params));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenArray$setReportTypes_whenQuery_thenShouldCallNativeApiWithEnumArgs() {
        // given
        PowerMockito.mockStatic(BugReporting.class);
        final Map<String, Object> args = new HashMap<>();
        ArgsRegistry.registerInstabugReportTypesArgs(args);
        final String[] keysArray = args.keySet().toArray(new String[0]);
        JavaOnlyArray actualArray = new JavaOnlyArray();
        actualArray.pushString(keysArray[0]);
        actualArray.pushString(keysArray[1]);
        // when
        rnModule.setReportTypes(actualArray);
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        int option1 = (int) args.get(keysArray[0]);
        int option2 = (int) args.get(keysArray[1]);
        BugReporting.setReportTypes(option1, option2);
    }

    @Test
    public void givenString$setVideoRecordingFloatingButtonPosition_whenQuery_thenShouldCallNativeApi() {
        // given
        PowerMockito.mockStatic(BugReporting.class);
        final Map<String, Object> args = new HashMap<>();
        ArgsRegistry.registerInstabugVideoRecordingFloatingButtonPositionArgs(args);
        final String[] keysArray = args.keySet().toArray(new String[0]);
        // when
        rnModule.setVideoRecordingFloatingButtonPosition(keysArray[0]);
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        InstabugVideoRecordingButtonPosition position = (InstabugVideoRecordingButtonPosition) args.get(keysArray[0]);
        BugReporting.setVideoRecordingFloatingButtonPosition(position);
    }

    @Test
    public void givenArgs$showBugReportingWithReportTypeAndOptions_whenQuery_thenShouldCallNativeApiWithEnums() {
        // given
        PowerMockito.mockStatic(BugReporting.class);
        final Map<String, Object> optionsArgs = new HashMap<>();
        final Map<String, Object> reportTypeArgs = new HashMap<>();
        ArgsRegistry.registerInvocationOptionsArgs(optionsArgs);
        ArgsRegistry.registerInstabugReportTypesArgs(reportTypeArgs);
        final String[] keysArray = optionsArgs.keySet().toArray(new String[0]);
        final String[] reportTypeKeys = reportTypeArgs.keySet().toArray(new String[0]);
        JavaOnlyArray actualArray = new JavaOnlyArray();
        actualArray.pushString(keysArray[0]);
        actualArray.pushString(keysArray[1]);
        // when
        rnModule.showBugReportingWithReportTypeAndOptions(reportTypeKeys[0], actualArray);
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        int option1 = (int) optionsArgs.get(keysArray[0]);
        int option2 = (int) optionsArgs.get(keysArray[1]);
        BugReporting.setOptions(option1, option2);
        BugReporting.show((int) reportTypeArgs.get(reportTypeKeys[0]));
    }

    /********CrashReporting*********/

    @Test
    public void givenFalse$CrashReportingEnabled_whenQuery_thenShouldCallNativeApiWithDisabled() {
        // given
        PowerMockito.mockStatic(CrashReporting.class);
        // when
        rnModule.setCrashReportingEnabled(false);
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        CrashReporting.setState(Feature.State.DISABLED);
    }

    @Test
    public void givenTrue$CrashReportingEnabled_whenQuery_thenShouldCallNativeApiWithEnabled() {
        // given
        PowerMockito.mockStatic(CrashReporting.class);
        // when
        rnModule.setCrashReportingEnabled(true);
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        CrashReporting.setState(Feature.State.ENABLED);
    }

    @Test
    public void givenString$sendHandledJSCrash_whenQuery_thenShouldCallNativeApiWithArgs() {

        try {
            JSONObject json = mock(JSONObject.class);
            PowerMockito.whenNew(JSONObject.class).withArguments("exception").thenReturn(json);

            // given
            PowerMockito.mockStatic(CrashReporting.class);
            // when
            rnModule.sendHandledJSCrash("exception");
            // then
            PowerMockito.verifyStatic(VerificationModeFactory.times(1));
            JSONObject jsonObject = new JSONObject("exception");
            Method method = getMethod(Class.forName("com.instabug.crash.CrashReporting"), "reportException", JSONObject.class, boolean.class, Report.class);
            if (method != null) {
                method.invoke(null, jsonObject, true, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    @Test
    public void givenString$sendJSCrash_whenQuery_thenShouldCallNativeApiWithArgs() {

        try {
            JSONObject json = mock(JSONObject.class);
            PowerMockito.whenNew(JSONObject.class).withArguments("exception").thenReturn(json);

            // given
            PowerMockito.mockStatic(CrashReporting.class);
            // when
            rnModule.sendJSCrash("exception");
            // then
            PowerMockito.verifyStatic(VerificationModeFactory.times(1));
            JSONObject jsonObject = new JSONObject("exception");
            Method method = getMethod(Class.forName("com.instabug.crash.CrashReporting"), "reportException", JSONObject.class, boolean.class, Report.class);
            if (method != null) {
                method.invoke(null, jsonObject, false, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    /********Surveys*********/

    @Test
    public void givenIsEnabled$setSurveysEnabled_whenQuery_thenShouldCallNativeApiWithIsEnabled() {
        // given
        PowerMockito.mockStatic(Surveys.class);
        // when
        rnModule.setSurveysEnabled(false);
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        Surveys.setAutoShowingEnabled(false);
    }

    @Test
    public void given$showSurveysIfAvailable_whenQuery_thenShouldCallNativeApi() {
        // given
        PowerMockito.mockStatic(Surveys.class);
        // when
        rnModule.showSurveysIfAvailable();
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        Surveys.showSurveyIfAvailable();
    }

    @Test
    public void givenIntArgs$setThresholdForReshowingSurveyAfterDismiss_whenQuery_thenShouldCallNativeApiWithIntArgs() {
        // given
        PowerMockito.mockStatic(Surveys.class);
        // when
        rnModule.setThresholdForReshowingSurveyAfterDismiss(2,2);
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        Surveys.setThresholdForReshowingSurveyAfterDismiss(2,2);
    }


    @Test
    public void given$getAvailableSurveys_whenQuery_thenShouldCallNativeApiAndInvokeCallback() throws Exception {
        // given
        PowerMockito.mockStatic(Surveys.class);
        PowerMockito.mockStatic(SystemClock.class);
        PowerMockito.mockStatic(Arguments.class);
        Callback callback = mock(Callback.class);
        JSONArray json = mock(JSONArray.class);
        // when
        PowerMockito.whenNew(JSONArray.class).withAnyArguments().thenReturn(json);
        PowerMockito.when(Arguments.createArray()).thenReturn(new JavaOnlyArray());
        rnModule.getAvailableSurveys(callback);
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        Surveys.getAvailableSurveys();
        verify(callback).invoke(any());
    }

    @Test
    public void givenIsEnabled$setAutoShowingSurveysEnabled_whenQuery_thenShouldCallNativeApiWithIsEnabled() {
        // given
        PowerMockito.mockStatic(Surveys.class);
        // when
        rnModule.setAutoShowingSurveysEnabled(true);
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        Surveys.setAutoShowingEnabled(true);
    }

    @Test
    public void given$setWillShowSurveyHandler_whenQuery_thenShouldSetNativeCallback() {

        try {
            // given
            PowerMockito.mockStatic(Surveys.class);
            RNInstabugReactnativeModule mockModule = mock(RNInstabugReactnativeModule.class);
            PowerMockito.doCallRealMethod().when(mockModule, "setWillShowSurveyHandler", Matchers.any());
            PowerMockito.doAnswer(new Answer<Object>() {
                @Override
                public Object answer(InvocationOnMock invocation) {
                    ((OnShowCallback) invocation.getArguments()[0]).onShow();
                    return null;
                }
            }).when(Surveys.class, "setOnShowCallback", Matchers.anyObject());
            // when
            mockModule.setWillShowSurveyHandler(null);
            // then
            verifyPrivate(mockModule, VerificationModeFactory.times(1))
                    .invoke("sendEvent", any(), eq("IBGWillShowSurvey"), eq(null));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void given$setDidDismissSurveyHandler_whenQuery_thenShouldSetNativeCallback() {
        try {
            // given
            PowerMockito.mockStatic(Surveys.class);
            RNInstabugReactnativeModule mockModule = mock(RNInstabugReactnativeModule.class);
            PowerMockito.doCallRealMethod().when(mockModule, "setDidDismissSurveyHandler", Matchers.any());
            PowerMockito.doAnswer(new Answer<Object>() {
                @Override
                public Object answer(InvocationOnMock invocation) {
                    ((OnDismissCallback) invocation.getArguments()[0]).onDismiss();
                    return null;
                }
            }).when(Surveys.class, "setOnDismissCallback", Matchers.anyObject());
            // when
            mockModule.setDidDismissSurveyHandler(null);
            // then
            verifyPrivate(mockModule, VerificationModeFactory.times(1))
                    .invoke("sendEvent", any(), eq("IBGDidDismissSurvey"), eq(null));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenString$showSurveyWithToken_whenQuery_thenShouldCallNativeApiWithString() {
        // given
        PowerMockito.mockStatic(Surveys.class);
        // when
        rnModule.showSurveyWithToken("123");
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        Surveys.showSurvey("123");
    }

    @Test
    public void givenBoolean$hasRespondedToSurveyWithToken_whenQuery_thenShouldCallNativeApiAndInvokeCallback() {
        // given
        PowerMockito.mockStatic(Surveys.class);
        // when
        Callback callback = mock(Callback.class);
        rnModule.hasRespondedToSurveyWithToken("123", callback);
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        Surveys.hasRespondToSurvey("123");
        verify(callback).invoke(any());
    }

    @Test
    public void givenBoolean$setShouldShowSurveysWelcomeScreen_whenQuery_thenShouldCallNativeApi() {
        // given
        PowerMockito.mockStatic(Surveys.class);
        // when
        rnModule.setShouldShowSurveysWelcomeScreen(true);
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        Surveys.setShouldShowWelcomeScreen(true);
    }

    /********Feature Requests*********/

    @Test
    public void givenArgs$setEmailFieldRequired_whenQuery_thenShouldCallNativeApi() {
        // given
        PowerMockito.mockStatic(FeatureRequests.class);
        PowerMockito.mockStatic(Arguments.class);
        // when
        PowerMockito.when(Arguments.createArray()).thenReturn(new JavaOnlyArray());
        ReadableArray actionTypes = Arguments.createArray();
        ((WritableArray) actionTypes).pushString("requestNewFeature");
        ((WritableArray) actionTypes).pushString("addCommentToFeature");
        rnModule.setEmailFieldRequiredForFeatureRequests(true, actionTypes );
        int[] parsedActionTypes = new int[2];
        parsedActionTypes[0] = ActionType.REQUEST_NEW_FEATURE;
        parsedActionTypes[1] = ActionType.ADD_COMMENT_TO_FEATURE;
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        FeatureRequests.setEmailFieldRequired(true, parsedActionTypes);
    }

    @Test
    public void given$showFeatureRequests_whenQuery_thenShouldCallNativeApi() {
        // given
        PowerMockito.mockStatic(FeatureRequests.class);
        // when
        rnModule.showFeatureRequests();
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        FeatureRequests.show();
    }

    /********Chats*********/

    @Test
    public void givenFalse$setChatsEnabled_whenQuery_thenShouldCallNativeApiWithDisabled() {
        // given
        PowerMockito.mockStatic(Chats.class);
        // when
        rnModule.setChatsEnabled(false);
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        Chats.setState(Feature.State.DISABLED);
    }

    @Test
    public void givenTrue$setChatsEnabled_whenQuery_thenShouldCallNativeApiWithEnabled() {
        // given
        PowerMockito.mockStatic(Chats.class);
        // when
        rnModule.setChatsEnabled(true);
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        Chats.setState(Feature.State.ENABLED);
    }

    @Test
    public void given$showChats_whenQuery_thenShouldCallNativeApi() {
        // given
        PowerMockito.mockStatic(Chats.class);
        // when
        rnModule.showChats();
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        Chats.show();
    }

    /********Replies*********/

    @Test
    public void givenFalse$setRepliesEnabled_whenQuery_thenShouldCallNativeApiWithDisabled() {
        // given
        PowerMockito.mockStatic(Replies.class);
        // when
        rnModule.setRepliesEnabled(false);
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        Replies.setState(Feature.State.DISABLED);
    }

    @Test
    public void givenTrue$setRepliesEnabled_whenQuery_thenShouldCallNativeApiWithEnabled() {
        // given
        PowerMockito.mockStatic(Replies.class);
        // when
        rnModule.setRepliesEnabled(true);
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        Replies.setState(Feature.State.ENABLED);
    }

    @Test
    public void givenCallback$hasChats_whenQuery_thenShouldCallNativeApiAndInvokeCallback() {
        // given
        PowerMockito.mockStatic(Replies.class);
        // when
        Callback callback = mock(Callback.class);
        rnModule.hasChats(callback);
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        Replies.hasChats();
        verify(callback).invoke(any());
    }

    @Test
    public void given$showReplies_whenQuery_thenShouldCallNativeApi() {
        // given
        PowerMockito.mockStatic(Replies.class);
        // when
        rnModule.showReplies();
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        Replies.show();
    }

    @Test
    public void givenCallback$getUnreadMessagesCount_whenQuery_thenShouldCallNativeApiAndInvokeCallback() {
        // given
        PowerMockito.mockStatic(Replies.class);
        // when
        Callback callback = mock(Callback.class);
        rnModule.getUnreadMessagesCount(callback);
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        Replies.getUnreadRepliesCount();
        verify(callback).invoke(any());
    }

    @Test
    public void givenBoolean$setChatNotificationEnabled_whenQuery_thenShouldCallNativeApi() {
        // given
        PowerMockito.mockStatic(Replies.class);
        // when
        rnModule.setChatNotificationEnabled(true);
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        Replies.setInAppNotificationEnabled(true);
    }

    @Test
    public void givenBoolean$setEnableInAppNotificationSound_whenQuery_thenShouldCallNativeApi() {
        // given
        PowerMockito.mockStatic(Replies.class);
        // when
        rnModule.setEnableInAppNotificationSound(true);
        // then
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        Replies.setInAppNotificationSound(true);
    }

}