package com.example.anand_roadwayss;

/*
 Developed By : prasob kumar k.p
 Created : 7-Jan-2014 04:00 PM
 Last Changed : 11-03-2014 03:50 PM
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.Interface.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SendToWebService extends AsyncTask<String, String, String> {
    boolean isAppServerIpConfigured = false;
    int interfaceToBeExecuted = 0, noDialog = 0;
    String responseData = " Error : netPrerequisites is become false ";
    boolean netPrerequisites = true;
    String methodToBeCalled;
    private static String authKey = null;
   // public static String regServerUrl;
    public static Boolean isDemoApp = false;
    private IRegistration mRegistration;
    private ITrackingVehicleTrip mTrackingVehicleTrip;
    private IDashBoardDriverChart mDashBoardDriverChart;
    private IDashBoardVehicleChart mDashBoardVehicleChart;
    private IDriverDistancePieChart mDriverDistancePieChart;
    private IVehicleDistancePieChart mVehicleDistancePieChart;
    private IAddAdvanceFragment mAddAdvanceFragment;
    private IDriverCleanerPayment mDriverCleanerPayment;
    private IFuelEntryFragment mFuelEntryFragment;
    private ITripManageFragment mTripManageFragment;
    private ITripListFragment mTripListFragment;
    private IAddLocation mAddLocation;
    private IDeleteLocation mDeleteLocation;
    private IAck mAck;
    private static IManageResources mInterfaceManageResources;
    private static IManageResourcesCleaner mInterfaceManageResourcesCleaner;
    private static IManageResourcesVehicle mInterfaceManageResourcesVehicle;
    private ILeaveEntryCleaner mLeaveEntryCleaneri;
    private ILeaveEntryDriver mLeaveEntryDriveri;
    private IInstallation mInstalled;
    private Synchronization mSync;
    private IDeletePayment mDeletePayment;
    private IDeleteAdvance mDeleteAdvance;
    private IConflictTrips mConflictTrips;
    private IAddExpense mAddExpense;
    private IDeleteExpense mDeleteExpense;
    private ITrackingStatus mTrackingStatus;
    private ITrackingReport mTrackingReport;
    private IFuelReport mFuelReport;
    private ISignature mSignature;
    private  ILiveTrack mLiveTrack;
    private Context _context;
    private IExpenseList mExpenseList;
    private IGetParticularTripData mGetParticularTripData;
    private IGetTripStatus mGetTripStatus;
    private IGetTripList mGetTripList;
    private IFuelGraph mFuelGraph;
    private IGetImeiNumbers mGetImeiNumbers;
    private IVehicleNotInTrip mVehicleNotInTrip;
    private IDriverNotInTrip mDriverNotInTrip;
    ProgressDialog dialog;

    public SendToWebService(Context context, int a) {
        _context = context;
        noDialog = a;
    }

    public SendToWebService(Context context) {
        _context = context;
        dialog = new ProgressDialog(_context);
    }

    public SendToWebService(Context context, IRegistration mRegistration) {
        _context = context;
        dialog = new ProgressDialog(_context);
        dialog.setMessage("Registering...");
        dialog.setCancelable(false);
        this.mRegistration = mRegistration;
    }

    public SendToWebService(Context context, ITrackingVehicleTrip mTrackingVehicleTrip) {
        _context = context;
        dialog = new ProgressDialog(_context);
        this.mTrackingVehicleTrip = mTrackingVehicleTrip;
    }

    public SendToWebService(Context context,
                            IDashBoardDriverChart mDashBoardDriverChart) {
        _context = context;
        dialog = new ProgressDialog(_context);
        this.mDashBoardDriverChart = mDashBoardDriverChart;
    }

    public SendToWebService(Context context, IAck mAck) {
        _context = context;
        dialog = new ProgressDialog(_context);
        this.mAck = mAck;
    }

    public SendToWebService(Context context,
                            IDashBoardVehicleChart mDashBoardVehicleChart) {
        _context = context;
        dialog = new ProgressDialog(_context);
        this.mDashBoardVehicleChart = mDashBoardVehicleChart;
    }

    public SendToWebService(Context context,
                            IDriverDistancePieChart mDriverDistancePieChart) {
        _context = context;
        dialog = new ProgressDialog(_context);
        this.mDriverDistancePieChart = mDriverDistancePieChart;
    }

    public SendToWebService(Context context,
                            IVehicleDistancePieChart mVehicleDistancePieChart) {
        _context = context;
        dialog = new ProgressDialog(_context);
        this.mVehicleDistancePieChart = mVehicleDistancePieChart;
    }

    public SendToWebService(Context context,
                            IAddAdvanceFragment mAddAdvanceFragment) {
        _context = context;
        dialog = new ProgressDialog(_context);
        this.mAddAdvanceFragment = mAddAdvanceFragment;
    }

    public SendToWebService(Context context, IDriverCleanerPayment mDriverCleanerPayment) {
        _context = context;
        dialog = new ProgressDialog(_context);
        this.mDriverCleanerPayment = mDriverCleanerPayment;
    }

    public SendToWebService(Context context,
                            IFuelEntryFragment mFuelEntryFragment) {
        _context = context;
        dialog = new ProgressDialog(_context);
        this.mFuelEntryFragment = mFuelEntryFragment;
    }

    public SendToWebService(Context context,
                            ITripManageFragment mTripManageFragment) {
        _context = context;
        dialog = new ProgressDialog(_context);
        this.mTripManageFragment = mTripManageFragment;
    }

    public SendToWebService(Context context, ITripListFragment mTripListFragment) {
        _context = context;
        dialog = new ProgressDialog(_context);
        this.mTripListFragment = mTripListFragment;
    }

    public SendToWebService(Context context, IAddLocation mAddLocation) {
        _context = context;
        dialog = new ProgressDialog(_context);
        this.mAddLocation = mAddLocation;
    }

    public SendToWebService(Context context, IDeleteLocation mDeleteLocation) {
        _context = context;
        dialog = new ProgressDialog(_context);
        this.mDeleteLocation = mDeleteLocation;
    }

    public SendToWebService(Context context,
                            IManageResources mfInterfaceManageResources) {
        _context = context;
        mInterfaceManageResources = mfInterfaceManageResources;
        dialog = new ProgressDialog(_context);
    }

    public SendToWebService(Context context,
                            IManageResourcesCleaner mfInterfaceManageResourcesCleaner) {
        _context = context;
        mInterfaceManageResourcesCleaner = mfInterfaceManageResourcesCleaner;
        dialog = new ProgressDialog(_context);
    }

    public SendToWebService(Context context,
                            IManageResourcesVehicle mfInterfaceManageResourcesVehicle) {
        _context = context;
        mInterfaceManageResourcesVehicle = mfInterfaceManageResourcesVehicle;
        dialog = new ProgressDialog(_context);
    }

    public SendToWebService(Context context, IInstallation installed) {
        _context = context;
        dialog = new ProgressDialog(_context);
        this.mInstalled = installed;
    }

    public SendToWebService(Context context, Synchronization Sync) {
        _context = context;
        dialog = new ProgressDialog(_context);
        this.mSync = Sync;
    }

    public SendToWebService(Context context, ILeaveEntryDriver mLeaveEntryDriver) {
        _context = context;
        interfaceToBeExecuted = 221;
        dialog = new ProgressDialog(_context);
        this.mLeaveEntryDriveri = mLeaveEntryDriver;
    }

    public SendToWebService(Context context,
                            ILeaveEntryCleaner mLeaveEntryCleaner) {
        _context = context;

        interfaceToBeExecuted = 222;
        dialog = new ProgressDialog(_context);
        this.mLeaveEntryCleaneri = mLeaveEntryCleaner;
    }

    public SendToWebService(Context context, IDeletePayment mDeletePayment) {
        _context = context;
        dialog = new ProgressDialog(_context);
        this.mDeletePayment = mDeletePayment;
    }

    public SendToWebService(Context context, IDeleteAdvance mDeleteAdvance) {
        _context = context;
        dialog = new ProgressDialog(_context);
        this.mDeleteAdvance = mDeleteAdvance;
    }

    public SendToWebService(Context context, IConflictTrips mConflictTrips) {
        _context = context;
        dialog = new ProgressDialog(_context);
        this.mConflictTrips = mConflictTrips;
    }

    public SendToWebService(Context context, IAddExpense  mAddExpense) {
        _context = context;
        dialog = new ProgressDialog(_context);
        this.mAddExpense = mAddExpense;
    }

    public SendToWebService(Context context, IDeleteExpense  mDeleteExpense)
    {
        _context = context;
        dialog = new ProgressDialog(_context);
        this.mDeleteExpense = mDeleteExpense;
    }

    public SendToWebService(Context context,ITrackingStatus mTrackingStatus)
    {
        _context=context;
        dialog=new ProgressDialog(_context);
        this.mTrackingStatus=mTrackingStatus;
    }

    public SendToWebService(Context context, ITrackingReport mTrackingReport) {
        _context = context;
        dialog = new ProgressDialog(_context);
        this.mTrackingReport = mTrackingReport;
    }
    public SendToWebService(Context context, IFuelReport mFuelReport)
    {
        _context = context;
        dialog = new ProgressDialog(_context);
        this.mFuelReport = mFuelReport;
    }
    public SendToWebService(Context context, ISignature mSignature) {
        _context = context;
        dialog = new ProgressDialog(_context);
        this.mSignature = mSignature;
    }
    public SendToWebService(Context context, ILiveTrack mLiveTrack) {
        _context = context;
        dialog = new ProgressDialog(_context);
        this.mLiveTrack = mLiveTrack;
    }
    public SendToWebService(Context context, IExpenseList  mExpenseList) {
        _context = context;
        dialog = new ProgressDialog(_context);
        this.mExpenseList = mExpenseList;
    }
    public SendToWebService(Context context, IGetParticularTripData  mGetParticularTripData) {
        _context = context;
        dialog = new ProgressDialog(_context);
        this.mGetParticularTripData = mGetParticularTripData;
    }
    public SendToWebService(Context context, IGetTripStatus  mGetTripStatus) {
        _context = context;
        dialog = new ProgressDialog(_context);
        this.mGetTripStatus = mGetTripStatus;
    }
    public SendToWebService(Context context, IGetTripList  mGetTripList) {
        _context = context;
        dialog = new ProgressDialog(_context);
        this.mGetTripList = mGetTripList;
    }

    public SendToWebService(Context context, IFuelGraph mFuelGraph)
    {
        _context = context;
        dialog = new ProgressDialog(_context);
        this.mFuelGraph = mFuelGraph;
    }

    public  SendToWebService(Context context,IVehicleNotInTrip mVehicleNotInTrip)
    {
        _context = context;
        dialog = new ProgressDialog(_context);
        this.mVehicleNotInTrip = mVehicleNotInTrip;
    }

    public  SendToWebService(Context context,IDriverNotInTrip mDriverNotInTrip)
    {
        _context = context;
        dialog = new ProgressDialog(_context);
        this.mDriverNotInTrip = mDriverNotInTrip;
    }

    public SendToWebService(Context context,IGetImeiNumbers mGetImeiNumbers)
    {
        _context=context;
        dialog=new ProgressDialog(_context);
        this.mGetImeiNumbers=mGetImeiNumbers;
    }


    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) _context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    // Helper method to execute pre-requirements.
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (noDialog != 1) {
            dialog.setTitle("Please wait...");
            dialog.show();
        }
    }

    @Override
    protected void onPostExecute(String response) {
        if (noDialog != 1) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            switch (interfaceToBeExecuted) {
                case 1:
                    mAddAdvanceFragment.onManageAdvanceDetailsTable(response);
                    break;

                case 2:
                    mAddLocation.onManageLocationTable(response);
                    break;

                case 5:
                    mDriverCleanerPayment.ongetCleanerPaymentDetails(response);
                    break;

                case 6:
                    mDriverCleanerPayment.ongetDriverPaymentDetails(response);
                    break;

                case 8:
                    mInterfaceManageResourcesVehicle
                            .onTaskCompleteVehicle(response);
                    break;

                case 9:
                    mRegistration.onRegistrationComplete(response);
                    break;

                case 10:
                    mInterfaceManageResources.onTaskComplete(response);
                    break;

                case 101:
                    mInterfaceManageResourcesCleaner
                            .onTaskCompleteCleaner(response);
                    break;

                case 11:
                    mTripManageFragment.onSaveSourceDestinationDetails(response);
                    break;

                case 13:
                    mDashBoardDriverChart.onDashBoardDriverChart(response);
                    break;

                case 14:
                    mDashBoardVehicleChart.onDashBoardVehicleChart(response);
                    break;

                case 15:
                    mTrackingVehicleTrip.onTrackingVehicleTrip(response);
                    break;

                case 16:
                    mFuelEntryFragment.onInsertToVehicleFuel(response);
                    break;

                case 17:
                    mDeleteLocation.onDeleteLocation(response);
                    break;

                case 18:
                    mDriverCleanerPayment.onManagePaymentDetails(response);
                    break;

                case 19:
                    mDriverDistancePieChart.onDriverDistancePieChart(response);
                    break;

                case 20:
                    mVehicleDistancePieChart.onVehicleDistancePieChart(response);
                    break;

                case 21:
                    mTripListFragment.onDeleteTrip(response);
                    break;

                case 221:
                    mLeaveEntryDriveri.onTaskCompleteDriverLeave(response);
                    break;

                case 222:
                    mLeaveEntryCleaneri.onTaskCompleteCleanerLeave(response);
                    break;

                case 23:
                    mAck.onFuelInLiters(response);
                    break;

                case 24:
                    mInstalled.iInstallation(response);
                    break;
                case 25:
                    mSync.onSynchronization(response);
                    break;

                case 26:
                    mDeletePayment.onDeletePayment(response);
                    break;

                case 27:
                    mDeleteAdvance.onDeleteAdvance(response);
                    break;

                case 28:
                    mConflictTrips.onConflictTrips(response);
                    break;

                case 29:
                    mAddExpense.onAddExpense(response);
                    break;

                case 30:
                    mDeleteExpense.onDeleteExpense(response);
                    break;

                case 31:mTrackingStatus.onTrackingStatus(response);
                    break;

                case 32:
                    mTrackingReport.onTrackingReport(response);
                    break;

                case 33:
                    mFuelReport.onFuelReport(response);
                    break;
                case 34:
                    mSignature.onAddSign(response);
                    break;
                case 35:
                    mExpenseList.onGetExpenseList(response);
                    break;

                case 36:
                    mGetTripList.onGetTripList(response);
                    break;

                case 37:
                    mGetParticularTripData.onGetParticularTripData(response);
                    break;

                case 38:mGetTripStatus.onGetTripStatus(response);
                    break;

                case 39:mLiveTrack.onTrackLive(response);
                    break;

                case 40:mFuelGraph.onFuelGraph(response);
                    break;

                case 46:mVehicleNotInTrip.onVehicleData(response);
                    break;

                case 47:mDriverNotInTrip.onDriverData(response);
                    break;

                case 48:mGetImeiNumbers.onGetImeiNums(response);
                default:
                    break;
            }
        }
    }

    @Override
    public String doInBackground(String... params) {

        try {


            String regServerUrl = "http://103.56.252.67:14100/fleet_registration_api.asmx/";

            SharedPreferences settings = _context.getSharedPreferences("AppUrl", 0);

            String prefixUrl = settings.getString("appUrl", "")
                    + "fleet_service_api.asmx/"; // Local

            // Server
            authKey = settings.getString("AuthKey", null);
            if (params[1] == "RegisterAnApplication" || params[1] == "ApplicationUpdateCheck" || params[1]=="GetClientsDevices") {
                prefixUrl = regServerUrl;
                isAppServerIpConfigured = true;
            }

             methodToBeCalled = params[0];// Value specifies which method
            // to be called.
            String url = prefixUrl + params[1];// Server url of the specific
            // method to be called.

            int methodNo = Integer.parseInt(methodToBeCalled);

            String methodN=params[1];

            // Creating an HTTP Post call, and passing the URL
            HttpPost httpPost = new HttpPost(url);

            httpPost.setHeader("content-type", "application/json");
            HttpClient httpClient = new DefaultHttpClient(getHttpParameterObj(
                    9000, 9000));

			/*
             * -----------------RECODED BY -SETHU-------------------------
			 * OFFLINE WEBSERVICE DIVERT FOR DEMO APPLICATION THIS DIVERT WILL
			 * SKIP THE SERVER CONNECT AND PROVIDE SAMPLE HARCODED JSON STRING
			 * TO response date
			 */
            OfflineWebService offlineWebService = new OfflineWebService();

            SharedPreferences UserType = _context.getSharedPreferences(
                    "RegisterName", 0);
            String UserTyp = UserType.getString("Name", "");
            if (UserTyp.equals("DEMO")) {
                isDemoApp = true;
            }

            switch (methodNo) {

                case 1:
                    interfaceToBeExecuted = 1;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    } else {
                        ManageAdvance(params[2], params[3], params[4],
                                params[5], params[6], params[7], params[8], params[9]);
                    }
                    break;

                case 2:
                    interfaceToBeExecuted = 2;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    } else {
                        ManageLocation(params[2], params[3], params[4],
                                params[5], params[6]);
                    }
                    break;

                case 3:
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    } else {
                        deleteAnEmployee(params[2]);
                    }
                    break;

                case 4:
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    } else {
                        deleteVehicle(params[2]);
                    }
                    break;

                case 5:
                    interfaceToBeExecuted = 5;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    } else {
                        getCleanerPaymentDetails(params[2], params[3], params[4]);
                    }
                    break;

                case 6:
                    interfaceToBeExecuted = 6;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    } else {
                        getDriverPaymentDetails(params[2], params[3], params[4]);
                    }
                    break;

                case 7:
                    getVehiclePaymentDetails(params[2], params[3], params[4]);
                    break;

                case 8:
                    interfaceToBeExecuted = 8;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    } else {
                        manageVehicle(params[2], params[3], params[4], params[5],
                                params[6], params[7],params[8]);
                    }
                    break;

                case 9:
                    //interfaceToBeExecuted = 9;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    } else {
                        RegisterAnApplication(params[2], params[3], params[4],
                                params[5], params[6], params[7], params[8],
                                params[9]);
                    }
                    break;

                case 10:
                    if (params[2] == "Driver")
                        interfaceToBeExecuted = 10;
                    if (params[2] == "Cleaner")
                        interfaceToBeExecuted = 101;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    } else {
                        ManageEmployee(params[2], params[3], params[4],
                                params[5], params[6], params[7], params[8],
                                params[9], params[10], params[11], params[12]);
                    }
                    break;

                case 11:
                    interfaceToBeExecuted = 11;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    } else {
                        saveTripDetails(params[2], params[3],
                                params[4], params[5], params[6], params[7],
                                params[8]);
                    }
                    break;

                case 12:
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    } else {
                        SaveGcmRegId(params[2]);
                    }
                    break;

                case 13:
                    interfaceToBeExecuted = 13;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    } else {
                        GetDriverMileage();
                    }
                    break;

                case 14:
                    interfaceToBeExecuted = 14;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    } else {
                        GetVehicleMileage();
                    }
                    break;

                case 15:
                    interfaceToBeExecuted = 15;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    } else {
                        GetTrackingTripDetails(params[2]);
                    }
                    break;

                case 16:
                    interfaceToBeExecuted = 16;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    } else {
                        SaveFuelDetails(params[2], params[3], params[4],
                                params[5], params[6]);
                    }
                    break;

                case 17:
                    interfaceToBeExecuted = 17;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    } else {
                        DeleteLocation(params[2]);
                    }
                    break;

                case 18:
                    interfaceToBeExecuted = 18;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    } else {
                        ManagePayment(params[2], params[3], params[4],
                                params[5], params[6], params[7], params[8],
                                params[9], params[10], params[11], params[12]);
                    }
                    break;
                case 19:
                    interfaceToBeExecuted = 19;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    } else {
                        GetDriverDistance(params[2], params[3]);
                    }
                    break;

                case 20:
                    interfaceToBeExecuted = 20;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    } else {
                        GetVehicleDistance(params[2], params[3]);
                    }
                    break;

                case 21:
                    interfaceToBeExecuted = 21;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    } else {
                        CloseVehicleOrDeleteTrip(params[2], params[3]);
                    }
                    break;

                case 22:
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    } else {
                        Absence(params[2], params[3]);
                    }
                    break;

                case 23:
                    interfaceToBeExecuted = 23;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    } else {
                        FuelInLiters(params[2], params[3]);
                    }
                    break;

                case 24:
                    interfaceToBeExecuted = 24;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    } else {
                        SyncCheckAfterInstall();
                    }
                    break;
                case 25:
                    interfaceToBeExecuted = 25;
                    DataSync(params[2]);
                    break;
                case 26:
                    ack(params[2]);
                    break;

                case 27:
                    interfaceToBeExecuted = 27;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    } else {
                        DeleteAdvance(params[2]);
                    }
                    break;

                case 28:

                    interfaceToBeExecuted = 26;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    } else {
                        DeleteEmployeePayment(params[2]);
                    }
                    break;
                case 29:
                    interfaceToBeExecuted = 16;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    } else {
                        DeleteFuel(params[2]);
                    }
                    break;

                case 30:
                    if (!isDemoApp) {
                        GetCurrentVoucherNumbers();
                    } else
                        responseData = null;
                    break;

                case 31:
                    interfaceToBeExecuted = 28;
                    if (!isDemoApp) {
                        GetConflictTrips();
                    } else
                        responseData = offlineWebService.fetchOfflineDataByMethodName(methodNo);
                    break;

                case 32:
                    if (!isDemoApp) {
                        ResolveConflictTrips(params[2], params[3], params[4],
                                params[5], params[6]);
                    } else
                        responseData = null;
                    break;

                case 33:
                    if (!isDemoApp) {
                        ApplicationUpdateCheck(params[2], params[3], params[4]);
                    } else {
                        responseData = offlineWebService.fetchOfflineDataByMethodName(methodNo);
                    }
                    break;

                case 34:
                    if (!isDemoApp){
                    SyncCheck();
                    }

                    else{
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    }

                    break;

                case 35:
                    interfaceToBeExecuted = 29;
                    if (isDemoApp) {
                    responseData = offlineWebService
                            .fetchOfflineDataByMethodName(methodNo);}
                    else {
                        ManageExpense(params[2], params[3], params[4], params[5], params[6], params[7], params[8], params[9], params[10]);
                    }
                    break;

                case 36:
                    interfaceToBeExecuted=33;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    }
                    else {
                        GetFuelReport(params[2], params[3], params[4]);
                    }
                    break;

                case 37:
                    interfaceToBeExecuted=30;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    }
                    else {
                        DeleteExpense(params[2]);
                    }
                    break;

                case 38:
                    interfaceToBeExecuted=31;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    }
                    else {
                        GetVehicleStatus();
                    }
                    break;

                case 39:
                    interfaceToBeExecuted=35;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    }
                    else {
                        GetExpenseList(params[2], params[3], params[4]);
                    }
                    break;

                case 40:
                    interfaceToBeExecuted=32;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    }
                    else {
                        GetTrackingReport(params[2], params[3], params[4]);
                    }
                    break;

                case 41:
                    interfaceToBeExecuted=34;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    }
                    else {
                        SaveSignature(params[2], params[3]);
                    }
                    break;

                case 42:
                    interfaceToBeExecuted=39;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    }
                    else {
                        GetLiveTracking(params[2]);
                    }
                    break;

                case 43:
                    interfaceToBeExecuted=36;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    }
                    else {
                        GetActiveTrips();
                    }
                    break;

                case 44:
                    interfaceToBeExecuted = 37;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    } else {
                        GetSubTrips(params[2], params[3]);
                    }
                    break;

                case 45:
                    interfaceToBeExecuted = 38;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    } else {
                        GetTripStatus(params[2]);
                    }
                    break;

                case 46:
                    interfaceToBeExecuted=47;
                    GetDriversNotInTrip();
                    break;

                case 47:
                    interfaceToBeExecuted=46;
                    GetVehiclesNotInTrip();
                    break;


                case 48:
                    interfaceToBeExecuted=11;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    } else {
                        UpdateSubTripDetails(params[2]);

                    }
                    break;

                case 49:
                    interfaceToBeExecuted = 40;
                    if (isDemoApp) {
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    }
                    else {
                        GetFuelData(params[2], params[3], params[4]);
                    }
                    break;

                case 50:
                    interfaceToBeExecuted=48;
                    if(isDemoApp){
                        responseData = offlineWebService
                                .fetchOfflineDataByMethodName(methodNo);
                    }
                    else{
                        GetClientsDevices(params[2]);
                    }
                    break;

                default:
                    defaultMethodCall();
                    break;
            }

            if (netPrerequisites & !isDemoApp) {
                if (isConnectingToInternet()) {
                    try {

                        StringEntity entity = new StringEntity(data.toString(),
                                HTTP.UTF_8);
                        httpPost.setEntity(entity);

                        // publishProgress("Progress Update : Connecting to server..");

                        // Creating the http call.
                        HttpResponse response = httpClient.execute(httpPost);
                        // publishProgress("Progress Update : Connected to server!");

                        // Getting data from the response to see if it was a
                        // success.
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(response.getEntity()
                                        .getContent(), "UTF-8"));
                        String jsonResultStr = reader.readLine();
                        data = new JSONObject(jsonResultStr);

                        if (data.toString() == "") {
                            responseData = "";
                        } else {
                            responseData = data.toString();
                        }
                    } catch (Exception e) {
                        // String
                        // exceptionMsg=ExcHandlingStackTraceToString.StackTraceToString(e);
                        // responseData =
                        // "Custom Exception in netPrerequisites Checks, Details : ==> "
                        // + exceptionMsg;
                        responseData = e.toString();

                    }
                } else {
                    responseData = "No Internet";
                }
            }


        } catch (Exception e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in doInBackground(String... params), Details : ==> "
                    + exceptionMsg;

            // ExceptionMessage.exceptionLog(this._context, this
            // .getClass().toString() + " " + "[deleteTrip]",
            // e.toString());

        }
        return responseData;
    }

    // GET HTTP PARAMETER OBJECT METHOD!

	/*
	 * Build HTTP Parameters, such as timeOut.
	 * 
	 * @param timeOutConnection
	 * 
	 * @param timeOutSocket
	 * 
	 * @return
	 */

    public HttpParams getHttpParameterObj(int timeOutConnection,
                                          int timeOutSocket) {

        HttpParams httpParameters = new BasicHttpParams();
        try {
            // Set the timeout in milliseconds until a connection is
            // established.
            HttpConnectionParams.setConnectionTimeout(httpParameters,
                    timeOutConnection);
            // Set the default socket timeout (SO_TIMEOUT)
            // in milliseconds which is the timeout for waiting for data.
            HttpConnectionParams.setSoTimeout(httpParameters, timeOutSocket);
        } catch (Exception e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in getHttpParameterObj(...), Details : ==> "
                    + exceptionMsg;
            netPrerequisites = false;
        }
        return httpParameters;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

        // code goes here
    }

    JSONObject data = new JSONObject();

    // -----------------------------------------------------
    // Method:1 in Switch Case
    public void ManageAdvance(String employeeId,
                                          String vehicleNumber, String advanceType, String advanceDate,
                                          String voucherNumber, String amount,String voucherImg, String statusFlag ) {

        interfaceToBeExecuted = 1;
        try {
            data.put("authKey", authKey);
            data.put("employeeId", employeeId);
            data.put("vehicleNumber", vehicleNumber);
            data.put("advanceType", advanceType);
            data.put("advanceDateTime", advanceDate);
            data.put("billNumber", voucherNumber);
            data.put("amount", amount);
            data.put("receipt",voucherImg);
            data.put("status", statusFlag);

        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in AddToAdvanceDetailsTable(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method:2 in Switch Case
    public void ManageLocation(String locationName, String locationType,
                                    String locationLatitude, String locationLongitude, String statusFlag) {
        interfaceToBeExecuted = 2;
        try {
            data.put("authKey", authKey);
            data.put("locationName", locationName);
            data.put("locationType", locationType);
            data.put("latitude", locationLatitude);
            data.put("longitude", locationLongitude);
            data.put("status", statusFlag);


        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in AddToLocationTable(...), Details : ==> "
                    + exceptionMsg;
        }

    }

    // Method:3 in Switch Case
    public void deleteAnEmployee(String employeeId) {
        try {
            data.put("authKey", authKey);
            data.put("employeeId", employeeId);

        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in deleteAnEmployee(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method:4 in Switch Case
    public void deleteVehicle(String vehicleNumber) {
        try {
            data.put("authKey", authKey);
            data.put("vehicleNumber", vehicleNumber);

        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in deleteVehicle(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method:5 in Switch Case
    public void getCleanerPaymentDetails(String employeeId,
                                         String fromDateTime, String toDateTime) {
        interfaceToBeExecuted = 5;
        try {
            data.put("authKey", authKey);
            data.put("employeeId", employeeId);
            data.put("fromDateTime", fromDateTime);
            data.put("toDateTime", toDateTime);
        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in getCleanerPaymentDetails(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method:6 in Switch Case
    public void getDriverPaymentDetails(String employeeId, String fromDateTime,
                                        String toDateTime) {
        interfaceToBeExecuted = 6;
        try {
            data.put("authKey", authKey);
            data.put("employeeId", employeeId);
            data.put("fromDateTime", fromDateTime);
            data.put("toDateTime", toDateTime);
        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in getDriverPaymentDetails(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method:7 in Switch Case
    public void getVehiclePaymentDetails(String vehicleNumber,
                                         String fromDateTime, String toDateTime) {
        try {
            data.put("authKey", authKey);
            data.put("vehicleNumber", vehicleNumber);
            data.put("fromDateTime", fromDateTime);
            data.put("toDateTime", toDateTime);
        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in getVehiclePaymentDetails(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method:8 in Switch Case
    private void manageVehicle(String vehicleNumber, String vehicleType,
                               String vehicleMileage, String IMEINumber, String deviceMobNumber,String protocol,
                               String statusFlag) {
        interfaceToBeExecuted = 8;
        try {
            data.put("authKey", authKey);
            data.put("vehicleNumber", vehicleNumber);
            data.put("vehicleType", vehicleType);
            data.put("standardMileage", vehicleMileage);
            data.put("deviceImei", IMEINumber);
            data.put("deviceMobileNumber", deviceMobNumber);
            data.put("protocol",protocol);
            data.put("status", statusFlag);

        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in trackingVehicleTrip(...), Details : ==> "
                    + exceptionMsg;
        }

    }

    // Method:9 in Switch Case
    public void RegisterAnApplication(String regAuthKey, String userName,
                                      String userRole, String emailId, String phoneNumber,String IMEI, String pin,
                                    String productKey) {

        interfaceToBeExecuted = 9;
        try {
            data.put("regAuthKey", regAuthKey);
            data.put("userName", userName);
            data.put("userRole", userRole);
            data.put("emailId", emailId);
            data.put("phoneNumber", phoneNumber);
            data.put("mobileImei", IMEI);
            data.put("pin", pin);
            data.put("productKey", productKey);
        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in registerAnApplication(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method:10 in Switch Case
    public void ManageEmployee(String employeeType, String employeeName,
                                 String userImage, String emailId, String employeeLicenceNumber,
                                 String employeePhoneNumber, String employeeAddress,
                                 String commission, String salary, String statusFlag,
                                 String UpdateEmpId) {
        if (employeeType == "Driver")
            interfaceToBeExecuted = 10;
        if (employeeType == "Cleaner")
            interfaceToBeExecuted = 101;
        try {
            data.put("authKey", authKey);
            data.put("employeeType", employeeType);
            data.put("employeeName", employeeName);
            data.put("userImage", userImage);
            data.put("emailId", emailId);
            data.put("licenceNumber", employeeLicenceNumber);
            data.put("phoneNumber", employeePhoneNumber);
            data.put("address", employeeAddress);
            data.put("commission", commission);
            data.put("salary", salary);
            data.put("status", statusFlag);
            data.put("updateEmployeeId", UpdateEmpId);
        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in registerAnEmployee(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method:11 in Switch Case
    public void saveTripDetails(String driverEmpId,
                                             String vehicleNumber, String cleanerEmpId, String sourceName,
                                String voucherNumber, String dateTime, String listValue) {
        interfaceToBeExecuted = 11;
        try {
            data.put("authKey", authKey);
            data.put("driverId", driverEmpId);
            data.put("vehicleNumber", vehicleNumber);
            data.put("cleanerId", cleanerEmpId);
            data.put("sourceName", sourceName);
            data.put("voucher", voucherNumber);
            data.put("tripDate", dateTime);
            data.put("arrayList", listValue);
            //  data.put("updateId",updateId);
        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in saveSourceDestinationDetails(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method:12 in Switch Case
    public void SaveGcmRegId(String gcmRegistrationId) {
        try {
            data.put("authKey", authKey);
            data.put("gcmRegId", gcmRegistrationId);
        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in saveUserDeviceMappingDetails(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method:13 in Switch Case
    public void GetDriverMileage() {
        interfaceToBeExecuted = 13;
        try {
            data.put("authKey", authKey);
        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in syncTableDriverMileage(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method:14 in Switch Case
    public void GetVehicleMileage() {
        interfaceToBeExecuted = 14;
        try {
            data.put("authKey", authKey);
        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in syncTableVehicleMileage(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method:15 in Switch Case
    public void GetTrackingTripDetails(String vehicleNumber) {
        interfaceToBeExecuted = 15;
        try {
            data.put("authKey", authKey);
            data.put("vehicleNumber", vehicleNumber);

        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in trackingVehicleTrip(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method:16 in Switch Case
    public void SaveFuelDetails(String vehicleNumber, String driverId,
                                    String distance, String fuel, String date) {
        interfaceToBeExecuted = 16;
        try {
            data.put("authKey", authKey);
            data.put("vehicleNumber", vehicleNumber);
            data.put("driverId", driverId);
            data.put("distance", distance);
            data.put("fuel", fuel);
            data.put("dateTime", date);
        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in trackingVehicleTrip(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method:17 in Switch Case
    public void DeleteLocation(String locationName) {
        interfaceToBeExecuted = 17;
        try {
            data.put("authKey", authKey);
            data.put("locationName", locationName);
        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in deleteLocation(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method:18 in Switch Case
    public void ManagePayment(String employeeId, String voucherNumber,
                                     String fromDate, String toDate, String commission,
                                     String paymentAmount, String paymentDate, String amountDeduction,
                                     String mileageDeduction ,String voucherImg,String statusFlag) {
        interfaceToBeExecuted = 18;
        try {
            data.put("authKey", authKey);
            data.put("employeeId", employeeId);
            data.put("billNumber", voucherNumber);
            data.put("fromDateTime", fromDate);
            data.put("toDateTime", toDate);
            data.put("commission", commission);
            data.put("amount", paymentAmount);
            data.put("paymentDateTime", paymentDate);
            data.put("amountDeduction", amountDeduction);
            data.put("mileageDeduction", mileageDeduction);
            data.put("receipt",voucherImg);
            data.put("status", statusFlag);

        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in managePaymentDetails(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method:19 in Switch Case
    public void GetDriverDistance(String fromDate, String toDate) {
        interfaceToBeExecuted = 19;
        try {
            data.put("authKey", authKey);
            data.put("fromDateTime", fromDate);
            data.put("toDateTime", toDate);
        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in DistanceTravelledByDriver(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method:20 in Switch Case
    public void GetVehicleDistance(String fromDate, String toDate) {
        interfaceToBeExecuted = 20;
        try {
            data.put("authKey", authKey);
            data.put("fromDateTime", fromDate);
            data.put("toDateTime", toDate);
        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in DistnaceTravelledByVehicle(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method:21 in Switch Case
    public void CloseVehicleOrDeleteTrip(String voucherNumber, String statusFlag) {
        interfaceToBeExecuted = 21;
        try {
            data.put("authKey", authKey);
            data.put("voucherNumber", voucherNumber);
            data.put("status", statusFlag);
        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in CloseVehicleTrip(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method:22 in Switch Case
    public void Absence(String employeeIds, String dateTime) {
        try {
            data.put("authKey", authKey);
            data.put("employeeIds", employeeIds);
            data.put("dateTime", dateTime);

        } catch (Exception e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in Absence(..), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method:23 in Switch Case
    public void FuelInLiters(String vehicleNumber, String fuel) {
        interfaceToBeExecuted = 23;
        try {
            data.put("authKey", authKey);
            data.put("vehicleNumber", vehicleNumber);
            data.put("fuel", fuel);

        } catch (Exception e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in FuelInLiters(..), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method:24 in Switch Case
    private void SyncCheckAfterInstall() {

        interfaceToBeExecuted = 24;
        try {
            data.put("authKey", authKey);
        } catch (Exception e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in SyncCheckAfterInstall(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method:25 in Switch Case
    private void DataSync(String processId) {

        interfaceToBeExecuted = 25;
        try {
            data.put("authKey", authKey);
            data.put("processId", processId);
        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in DataSync(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method:26 in Switch Case
    private void ack(String processId) {

        // interfaceToBeExecuted = 26;
        try {
            data.put("authKey", authKey);
            data.put("processId", processId);
        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in ack(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method: 27 in Switch Case

    public void DeleteAdvance(String voucherNumber) {
        interfaceToBeExecuted = 27;
        try {
            data.put("authKey", authKey);
            data.put("billNumber", voucherNumber);
        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in deleteEmployeeAdvance(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method: 28 in Switch Case

    public void DeleteEmployeePayment(String voucherNumber) {
        interfaceToBeExecuted = 26;
        try {
            data.put("authKey", authKey);
            data.put("billNumber", voucherNumber);
        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in deleteEmployeePayment(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method:29 in Switch Case
    private void DeleteFuel(String rowId) {

        // interfaceToBeExecuted = 26;
        try {
            data.put("authKey", authKey);
            data.put("rowId", rowId);
        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in DataSync(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method:30 in Switch Case
    private void GetCurrentVoucherNumbers() {
        try {
            data.put("authKey", authKey);
        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in GetCurrentVoucherNumbers(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method:31 in Switch Case
    private void GetConflictTrips() {
        try {
            data.put("authKey", authKey);
        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in GetConflictTrips(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method:32 in Switch Case
    private void ResolveConflictTrips(String voucher, String pay,
                                      String destination, String driverId, String cleanerId) {
        try {
            data.put("authKey", authKey);
            data.put("voucherNo", voucher);
            data.put("allowPayment", pay);
            data.put("destination", destination);
            data.put("driverId", driverId);
            data.put("cleanerId", cleanerId);

        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in UpdateConflictTrips(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method:33 in Switch Case
    private void ApplicationUpdateCheck(String authKey, String deviceIMEI,
                                        String currentVersion) {
        try {
            data.put("regAuthKey", authKey);
            data.put("Imei", deviceIMEI);
            data.put("currentVersion", currentVersion);

        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in ApplicationUpdateCheck(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method:34 in Switch Case
    private void SyncCheck() {
        try {
            data.put("authKey", authKey);

        } catch (JSONException e) {
            // String
            // exceptionMsg=ExcHandlingStackTraceToString.StackTraceToString(e);
            responseData = "Custom Exception in SyncCheck(...), Details : ==> "
                    + e.getMessage();
        }
    }

    // Method:35 in Switch Case
    public void ManageExpense(String employeeId,
                                          String vehicleNumber, String expenseDate,
                                          String voucherNumber,String particular, String amount, String expenseReceipt,String statusFlag,String updateExpenseId) {

        interfaceToBeExecuted = 29;
        try {
            data.put("authKey", authKey);
            data.put("driverId", employeeId);
            data.put("vehicleNumber", vehicleNumber);
            data.put("expenseDateTime", expenseDate);
            data.put("billNumber", voucherNumber);
            data.put("particular", particular);
            data.put("amount", amount);
            data.put("receipt", expenseReceipt);
            data.put("status", statusFlag);
            data.put("updateExpenseId",updateExpenseId);

        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in AddToAdvanceDetailsTable(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method: 36 in Switch Case

    public void DeleteExpense(String voucherNumber) {
        interfaceToBeExecuted = 30;
        try {
            data.put("authKey", authKey);
            data.put("billNumber", voucherNumber);
        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in deleteEmployeeAdvance(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    // Method: 37 in Switch Case

    public void GetVehicleStatus() {
        interfaceToBeExecuted = 31;
        try {
            data.put("authKey", authKey);

        } catch (JSONException e) {
            responseData = "Custom Exception in SyncCheck(...), Details : ==> "
                    + e.getMessage();
        }
    }


    // Method: 38 in Switch Case
    public void GetExpenseList(String fromDate, String toDate,String vehicleNumber) {
        try {
            data.put("authKey", authKey);
            data.put("fromDateTime", fromDate);
            data.put("toDateTime", toDate);
            data.put("vehicleNumber", vehicleNumber);
        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in deleteEmployeeAdvance(...), Details : ==> "
                    + exceptionMsg;
        }
    }
    //Method:39 in switch case
    public void GetTrackingReport(String fromDate, String toDate,String vehicleNum) {
        interfaceToBeExecuted = 32;
        try {

            data.put("authKey", authKey);
            data.put("fromDateTime", fromDate);
            data.put("toDateTime", toDate);
            data.put("vehicleNumber",vehicleNum);

        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in TrackingReport(...), Details : ==> "
                    + exceptionMsg;
        }
    }
    //Method:40 in Switch case
    public void GetFuelReport(String fromDate, String toDate,String vehicleNum) {
        interfaceToBeExecuted = 33;
        try {
            data.put("authKey", authKey);
            data.put("fromDateTime", fromDate);
            data.put("toDateTime", toDate);
            data.put("vehicleNumber",vehicleNum);

        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in FuelReport(...), Details : ==> "
                    + exceptionMsg;
        }
    }
    //Method:41 in switch case
    public void SaveSignature( String signImg,String status )
    {
        interfaceToBeExecuted = 34;
        try{
            data.put("authKey", authKey);
            data.put("signature", signImg);
            data.put("status",status);
        }
        catch(JSONException e){
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in addSign(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    //Method:42 in switch case
    public void GetLiveTracking( String vehNum )
    {
        interfaceToBeExecuted = 39;
        try{
            data.put("authKey", authKey);
            data.put("vehicleNumber", vehNum);
        }
        catch(JSONException e){
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in addSign(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    //Method:42 in switch case
    public void GetActiveTrips()
    {

        interfaceToBeExecuted = 36;
        try{
            data.put("authKey", authKey);
        }
        catch(JSONException e){
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in addSign(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    //Method:43

    public void GetSubTrips(String voucher, String vehicleNo) {

        interfaceToBeExecuted = 37;
        try {
            data.put("authKey", authKey);
            data.put("vehicleNumber", vehicleNo);
            data.put("voucherNumber", voucher);
        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in addSign(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    //Method 44
    public void GetTripStatus(String voucher) {

        interfaceToBeExecuted = 38;
        try {
            data.put("authKey", authKey);
            data.put("voucherNumber", voucher);
        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in addSign(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    //Method 45
    public void GetDriversNotInTrip() {
interfaceToBeExecuted=47;
        try {
            data.put("authKey", authKey);
        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in addSign(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    //Method 46
    public void GetVehiclesNotInTrip() {
        interfaceToBeExecuted=46;
        try {
            data.put("authKey", authKey);
        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in addSign(...), Details : ==> "
                    + exceptionMsg;
        }
    }

    public void GetFuelData(String fromDate, String toDate, String vehicleNum) {
        interfaceToBeExecuted = 40;
        try {
            data.put("authKey", authKey);
            data.put("vehicleNumber",vehicleNum);
            data.put("fromDateTime", fromDate);
            data.put("toDateTime", toDate);


        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in FuelGraph(...), Details : ==> "
                    + exceptionMsg;
        }
    }

       //Method 47
    public void UpdateSubTripDetails(String listValue) {

        try {
            data.put("authKey", authKey);
            data.put("arrayList", listValue);
        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in addSign(...), Details : ==> "
                    + exceptionMsg;
        }
    }


    //Method 48
    public void GetClientsDevices(String clientName)
    {
        interfaceToBeExecuted=48;

        try {
            data.put("regAuthKey", "regmnk");
            data.put("appAuthKey", "appmnk");
            data.put("clientName",clientName);
        } catch (JSONException e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in addSign(...), Details : ==> "
                    + exceptionMsg;
        }
    }


    // Method:41 in Switch Case
    public void defaultMethodCall() {
        try {
            responseData = "Custom Error : Default method called in Switch [ Reason : 'methodNo' Value is invalid in send.execute(params..) method]";
            netPrerequisites = false; // To avoid http post in case of invalid
            // data.

        } catch (Exception e) {
            String exceptionMsg = ExcHandlingStackTraceToString
                    .StackTraceToString(e);
            responseData = "Custom Exception in defaultMethodCall(), Details : ==> "
                    + exceptionMsg;
        }
    }
}