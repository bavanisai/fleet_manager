package com.example.anand_roadwayss;

/**
 * Created by Sooryakrishnakp on 15-07-2014.
 */
public class OfflineWebService {
    public String fetchOfflineDataByStatusFlag(String methodToBeCalled) {
        String offlineData = null;
        switch (Integer.parseInt(methodToBeCalled)) {
            case 0:
                offlineData = "\"\\\"Deleted\\\"\"";
                break;

            case 1:
                offlineData = "\"\\\"Inserted\\\"\"";
                break;

            case 4:
                offlineData = "\"\\\"Inserted\\\"\"";
                break;

            default:
                offlineData = "\"\\\"Error\\\"\"";
                break;
        }
        // return "{\"d\":" + offlineData + "}";
        return "{\"d\":" + offlineData + "}";
    }

    public String fetchOfflineDataByMethodName(int methodToBeCalled) {
        String offlineData = null;
        switch (methodToBeCalled) {
            case 1: // ManageAdvance
                offlineData = "{\"status\":\"inserted\"}";
                break;

            case 2: // ManageLocation
                offlineData = "{\"status\":\"inserted\"}";
                break;

            case 3: // deleteAnEmployee
                offlineData = "{\"status\":\"deleted\",\"employeeId\":\"1\"}";
                break;
            case 4: // deleteVehicle
                offlineData ="{\"status\":\"deleted\",\"vehicleId\":\"1\"}";
                break;
            case 5: // getCleanerPaymentDetails
                offlineData ="[{\"status\":\"OK\"},[{\"vehicleNumber\":\"TN 63 AC 1643 \",\"driver\":\"RAM\",\"tripStartDate\":\"2015-08-25T00:00:00\",\"sourceName\":\"Devasandra\",\"destinationName\":\"Sadashivnagar\",\"totalKm\":90.0,\"tripAmount\":5000.00},{\"vehicleNumber\":\"TN 63 AC 1643 \",\"driver\":\"RAM\",\"tripStartDate\":\"2015-08-25T00:00:00\",\"sourceName\":\"Devasandra\",\"destinationName\":\"Sadashivnagar\",\"totalKm\":119.0,\"tripAmount\":5000.00},{\"vehicleNumber\":\"TN 63 AC 1644\",\"driver\":\"MANISH\",\"tripStartDate\":\"2015-08-25T00:00:00\",\"sourceName\":\"Devasandra\",\"destinationName\":\"Sadashivnagar\",\"totalKm\":220.0,\"tripAmount\":9000.00},{\"vehicleNumber\":\"TN 63 AC 1645\",\"driver\":\"MADAN\",\"tripStartDate\":\"2015-08-25T00:00:00\",\"sourceName\":\"Devasandra\",\"destinationName\":\"Rmv hospital\",\"totalKm\":100.0,\"tripAmount\":5000.00},{\"vehicleNumber\":\"TN 63 AC 1643 \",\"driver\":\"RAM\",\"tripStartDate\":\"2015-08-26T00:00:00\",\"sourceName\":\"Devasandra\",\"destinationName\":\"Sadashivnagar\",\"totalKm\":300.0,\"tripAmount\":3000.00},{\"vehicleNumber\":\"TN 63 AC 1645\",\"driver\":\"MANISH\",\"tripStartDate\":\"2015-08-26T00:00:00\",\"sourceName\":\"Devasandra\",\"destinationName\":\"Rmv hospital\",\"totalKm\":100.0,\"tripAmount\":1000.00}],[{\"advanceDate\":\"2015-08-25T00:00:00\",\"advanceAmount\":200.00},{\"advanceDate\":\"2015-08-26T00:00:00\",\"advanceAmount\":300.00}],[{\"salary\":4129.03},{\"commission\":4.00}]]";
                break;
            case 6: // getDriverPaymentDetails
                offlineData = "[{\"status\":\"OK\"},[{\"vehicleNumber\":\"TN 63 AC 1643 \",\"cleaner\":\"DAMAN\",\"tripStartDate\":\"2015-08-25T00:00:00\",\"sourceName\":\"Devasandra\",\"destinationName\":\"Sadashivnagar\",\"totalKm\":90.0,\"tripAmount\":5000.00},{\"vehicleNumber\":\"TN 63 AC 1643 \",\"cleaner\":\"DAMAN\",\"tripStartDate\":\"2015-08-25T00:00:00\",\"sourceName\":\"Devasandra\",\"destinationName\":\"Sadashivnagar\",\"totalKm\":119.0,\"tripAmount\":5000.00},{\"vehicleNumber\":\"TN 63 AC 1643 \",\"cleaner\":\"DAMAN\",\"tripStartDate\":\"2015-08-26T00:00:00\",\"sourceName\":\"Devasandra\",\"destinationName\":\"Sadashivnagar\",\"totalKm\":300.0,\"tripAmount\":3000.00}],[{\"vehicleNumber\":\"TN 63 AC 1643 \",\"advanceDate\":\"2015-08-26T00:00:00\",\"advanceAmount\":300.00}],[{\"driver\":\"RAM\",\"advacnceDate\":\"2015-08-26T00:00:00\",\"advanceAmount\":600.00}],[{\"vehicleNumber\":\"TN 63 AC 1643 \",\"fuelConsumed\":500.0,\"standardMileage\":10.0,\"givenMileage\":0.0}],[{\"salary\":5161.29},{\"commission\":8.00},{\"expenseAmount\":600.00}]]";
                break;
            case 7: // getVehiclePaymentDetails
                offlineData = "";/*-------------This method has been removed from server-------------*/
                break;
            case 8: // manageVehicle
                offlineData = "{\"status\":\"inserted\",\"vehicleNumber\":\"TN 63 AC 1643 \"}";
                break;
            case 9: // registerAnApplication
                offlineData = "{\"status\":\"updated\",\"appAuthKey\":\"443wS07NH15M417eQ33\",\"ipAddress\":\"103.56.252.67\",\"portNumber\":\"15100\",\"clientName\":\"MNK SOFTWARE\"}";
                break;
            case 10: // ManageAnEmployee
                offlineData = "{\"status\":\"inserted\",\"employeeId\":\"1\"}";
                break;
            case 11: // saveSourceDestinationDetails
                offlineData = "{\"status\":\"inserted\"}";
                break;
            case 12: // SaveUserDeviceMappingDetails
                offlineData = "{\"status\":\"inserted\"}";
                break;
            case 13: // syncTableDriverMileage
                offlineData = "[{\"status\":\"OK\"},[{\"id\":\"RAM \",\"listYear\":[{\"year\":\"2015\",\"jan\":null,\"feb\":null,\"mar\":null,\"apr\":null,\"may\":null,\"jun\":null,\"jul\":\"5\",\"aug\":\"6\",\"sep\":\"7\",\"oct\":null,\"nov\":null,\"dec\":null}]},{\"id\":\"MADAN \",\"listYear\":[{\"year\":\"2015\",\"jan\":null,\"feb\":null,\"mar\":null,\"apr\":null,\"may\":null,\"jun\":null,\"jul\":\"5\",\"aug\":\"6\",\"sep\":\"7\",\"oct\":null,\"nov\":null,\"dec\":null}]},{\"id\":\"MANISH \",\"listYear\":[{\"year\":\"2015\",\"jan\":null,\"feb\":null,\"mar\":null,\"apr\":null,\"may\":null,\"jun\":null,\"jul\":\"5\",\"aug\":\"6\",\"sep\":\"7\",\"oct\":null,\"nov\":null,\"dec\":null}]}]]";
                break;
            case 14: // syncTableVehicleMileage
                offlineData = "[{\"status\":\"OK\"},[{\"id\":\"TN 63 AC 1643 \",\"listYear\":[{\"year\":\"2015\",\"jan\":null,\"feb\":null,\"mar\":null,\"apr\":null,\"may\":null,\"jun\":null,\"jul\":\"5\",\"aug\":\"6\",\"sep\":\"7\",\"oct\":null,\"nov\":null,\"dec\":null}]},{\"id\":\"TN 63 AC 1644 \",\"listYear\":[{\"year\":\"2015\",\"jan\":null,\"feb\":null,\"mar\":null,\"apr\":null,\"may\":null,\"jun\":null,\"jul\":\"5\",\"aug\":\"6\",\"sep\":\"7\",\"oct\":null,\"nov\":null,\"dec\":null}]},{\"id\":\"TN 63 AC 1645 \",\"listYear\":[{\"year\":\"2015\",\"jan\":null,\"feb\":null,\"mar\":null,\"apr\":null,\"may\":null,\"jun\":null,\"jul\":\"5\",\"aug\":\"6\",\"sep\":\"7\",\"oct\":null,\"nov\":null,\"dec\":null}]}]]";
                break;
            case 15: // trackingVehicleTrip
                offlineData = "[{\"status\":\"OK\"},[{\"sourceName\":\"Devasandra\",\"sourceLat\":\"13.000334\",\"sourceLong\":\"77.563739\",\"currDateTime\":\"2015-08-25T16:09:00.543\"}],[{\"driverName\":\"RAM\"}],[{\"destinationName\":\"Rmv hospital\",\"tripStatus\":1,\"destinationLat\":\"12.994908\",\"destinationLong\":\"77.567365\"}],[{\"vehicleStatus\":\"Running\",\"runningTime\":\"1 days, 6 hrs, 3 min \",\"distance\":\"100\"},{\"currLatitude\":\"12.998556\",\"currLongitude\":\"77.564812\",\"currFuel\":100.0,\"currSpeed\":40.0,\"tripStatus\":\"on trip\",\"receivedDateTime\":\"Aug 18 2015 11:20AM\"}]]";
                break;
            case 16: // insertToVehicleFuel
                offlineData = "{\"status\":\"inserted\",\"id\":\"1\"}";
                break;
            case 17: // deleteLocation
                offlineData = "{\"status\":\"deleted\"}";
                break;
            case 18: // managePaymentDetails
                offlineData = "{\"status\":\"inserted\"}"; /*-------------Based on status flag-------------*/
                break;
            case 19: // GetDriverDistance
                offlineData = "[{\"status\":\"OK\"},{\"driverName\":\"MADAN\",\"distance\":100.0},{\"driverName\":\"MANISH\",\"distance\":320.0},{\"driverName\":\"RAM\",\"distance\":509.0}]";
                break;
            case 20: // GetVehicleDistance
                offlineData = "[{\"status\":\"OK\"},{\"driverName\":\"TN 63 AC 1643 \",\"distance\":509.0},{\"driverName\":\"TN 63 AC 1644\",\"distance\":220.0},{\"driverName\":\"TN 63 AC 1645\",\"distance\":200.0}]";
                break;
            case 21: // CloseVehicleTrip
                offlineData = "{\"status\":\"closed\"}";
                break;
            case 22: // Absence
                offlineData = "{\"status\":\"updated\"}";
                break;
            case 23: // FuelInLiters
                offlineData = "{\"status\":\"inserted\"}";
                break;
            case 24: // SyncCheckAfterInstall
                offlineData = "{\"status\":\"data does not exist\"}";
                break;
            case 27: // DeleteEmployeeAdvance
                offlineData = "{\"status\":\"deleted\"}";
                break;
            case 28: // DeleteEmployeePayment
                offlineData = "{\"status\":\"deleted\"}";
                break;
            case 29: // DeleteFuel
                offlineData = "{\"status\":\"deleted\"}";
                break;
            case 30 : //GetCurrentVoucherNumbers
                offlineData = "";// not used
                break;
            case 31 : //GetConflictTrips()
                offlineData ="[{\"status\":\"OK\"},{\"tripDate\":\"2015-08-25T00:00:00\",\"vehicleNumber\":\"TN 63 AC 1643 \",\"sourceName\":\"Devasandra\",\"destinationName\":\"Sadashivnagar\",\"distance\":90.0,\"amount\":5000.00,\"status\":\"pending\"},{\"tripDate\":\"2015-08-25T00:00:00\",\"vehicleNumber\":\"TN 63 AC 1643 \",\"sourceName\":\"Devasandra\",\"destinationName\":\"Sadashivnagar\",\"distance\":119.0,\"amount\":5000.00,\"status\":\"pending\"},{\"tripDate\":\"2015-08-25T00:00:00\",\"vehicleNumber\":\"TN 63 AC 1644\",\"sourceName\":\"Devasandra\",\"destinationName\":\"Sadashivnagar\",\"distance\":220.0,\"amount\":9000.00,\"status\":\"pending\"},{\"tripDate\":\"2015-08-25T00:00:00\",\"vehicleNumber\":\"TN 63 AC 1645\",\"sourceName\":\"Devasandra\",\"destinationName\":\"Rmv hospital\",\"distance\":100.0,\"amount\":5000.00,\"status\":\"pending\"},{\"tripDate\":\"2015-08-26T00:00:00\",\"vehicleNumber\":\"TN 63 AC 1643 \",\"sourceName\":\"Devasandra\",\"destinationName\":\"Sadashivnagar\",\"distance\":300.0,\"amount\":3000.00,\"status\":\"pending\"},{\"tripDate\":\"2015-08-26T00:00:00\",\"vehicleNumber\":\"TN 63 AC 1645\",\"sourceName\":\"Devasandra\",\"destinationName\":\"Rmv hospital\",\"distance\":100.0,\"amount\":1000.00,\"status\":\"pending\"}]";
                break;
            case 32 : //ResolveConflictTrips
                offlineData="";
            case 33 : //ApplicationUpdateCheck
                offlineData = "{\"status\":\"Server App Version is lesser than yours\",\"updateUrl\":null}";
                break;
            case 34 : //SyncCheck
                offlineData="{\"status\":\"no data\"}";
                break;
            case 35 : //ManageExpense
                offlineData = "{\"status\":\"inserted\"}";
                break;
            case 36 : //GetFuelReport;
                offlineData = "[{\"status\":\"OK\"},{\"averageMileage\":8},{\"fuelFilledDate\":\"2015-08-25T00:00:00\",\"vehicleNumber\":\"TN 63 AC 1643 \",\"employeeName\":\"RAM\",\"fuelVolume\":500.0,\"speedoMeter\":10000.0},{\"fuelFilledDate\":\"2015-08-25T00:00:00\",\"vehicleNumber\":\"TN 63 AC 1643 \",\"employeeName\":\"RAM\",\"fuelVolume\":20.0,\"speedoMeter\":10010.0},{\"fuelFilledDate\":\"2015-08-25T00:00:00\",\"vehicleNumber\":\"TN 63 AC 1643 \",\"employeeName\":\"RAM\",\"fuelVolume\":20.0,\"speedoMeter\":10010.0},{\"fuelFilledDate\":\"2015-08-26T00:00:00\",\"vehicleNumber\":\"TN 63 AC 1643 \",\"employeeName\":\"RAM\",\"fuelVolume\":500.0,\"speedoMeter\":10100.0},{\"fuelFilledDate\":\"2015-08-27T00:00:00\",\"vehicleNumber\":\"TN 63 AC 1643 \",\"employeeName\":\"RAM\",\"fuelVolume\":500.0,\"speedoMeter\":10200.0},{\"fuelFilledDate\":\"2015-08-28T00:00:00\",\"vehicleNumber\":\"TN 63 AC 1643 \",\"employeeName\":\"RAM\",\"fuelVolume\":200.0,\"speedoMeter\":10250.0},{\"fuelFilledDate\":\"2015-08-28T00:00:00\",\"vehicleNumber\":\"TN 63 AC 1643 \",\"employeeName\":\"RAM\",\"fuelVolume\":100.0,\"speedoMeter\":10280.0},{\"fuelFilledDate\":\"2015-08-29T00:00:00\",\"vehicleNumber\":\"TN 63 AC 1643 \",\"employeeName\":\"RAM\",\"fuelVolume\":200.0,\"speedoMeter\":10300.0},{\"fuelFilledDate\":\"2015-08-29T00:00:00\",\"vehicleNumber\":\"TN 63 AC 1643 \",\"employeeName\":\"RAM\",\"fuelVolume\":100.0,\"speedoMeter\":10320.0},{\"fuelFilledDate\":\"2015-08-29T00:00:00\",\"vehicleNumber\":\"TN 63 AC 1643 \",\"employeeName\":\"RAM\",\"fuelVolume\":500.0,\"speedoMeter\":10350.0}]";
                break;
            case 37 : //DeleteExpense
                offlineData = "{\"status\":\"deleted\"}";
                break;
            case 38 : //GetVehicleStatus
                offlineData = "[{\"status\":\"OK\"},{\"vehicleNumber\":\"KA001\",\"vehicleStatus\":\"Jul 3 2015 9:45AM\"},{\"vehicleNumber\":\"KA003\",\"vehicleStatus\":\"Jul 3 2015 9:45AM\"},{\"vehicleNumber\":\"KA009\",\"vehicleStatus\":\"Jul 3 2015 9:45AM\"},{\"vehicleNumber\":\"KA008\",\"vehicleStatus\":\"Jul 3 2015 9:45AM\"}]";
                break;
            case 39 : //GetExpenseList
                offlineData = "[{\"status\":\"OK\"},{\"expenseId\":1,\"employeeName\":\"RAM\",\"expenseDate\":\"2015-08-26T00:00:00\",\"particular\":\"Tollgate\",\"billNumber\":\"10006\",\"amount\":200.00,\"receipt\":\"\"},{\"expenseId\":2,\"employeeName\":\"RAM\",\"expenseDate\":\"2015-08-26T00:00:00\",\"particular\":\"Tollgate \",\"billNumber\":\"10005\",\"amount\":300.00,\"receipt\":\"\"},{\"expenseId\":3,\"employeeName\":\"RAM\",\"expenseDate\":\"2015-08-26T00:00:00\",\"particular\":\"Tollgate \",\"billNumber\":\"10004\",\"amount\":100.00,\"receipt\":\"\"}]";
                break;
            case 40 : //GetTrackingReport
                offlineData ="[{\"status\":\"OK\"},{\"address\":\"Chennai\",\"deviceDate\":\"2015-08-18T00:00:00\",\"deviceTime\":\"11:17:06 AM\",\"speed\":40.00,\"vehicleStatus\":1},{\"address\":\"Chennai\",\"deviceDate\":\"2015-08-18T00:00:00\",\"deviceTime\":\"11:35:59 AM\",\"speed\":40.00,\"vehicleStatus\":1},{\"address\":\"Chennai\",\"deviceDate\":\"2015-08-18T00:00:00\",\"deviceTime\":\"11:56:59 AM\",\"speed\":50.00,\"vehicleStatus\":1},{\"address\":\"Chennai\",\"deviceDate\":\"2015-08-18T00:00:00\",\"deviceTime\":\"12:17:43 PM\",\"speed\":50.00,\"vehicleStatus\":1},{\"address\":\"Chennai\",\"deviceDate\":\"2015-08-18T00:00:00\",\"deviceTime\":\"12:37:43 PM\",\"speed\":60.00,\"vehicleStatus\":1},{\"address\":\"Chennai\",\"deviceDate\":\"2015-08-25T00:00:00\",\"deviceTime\":\" 5:04:30 PM\",\"speed\":55.00,\"vehicleStatus\":1},{\"address\":\"Chennai\",\"deviceDate\":\"2015-08-25T00:00:00\",\"deviceTime\":\" 5:24:10 PM\",\"speed\":50.58,\"vehicleStatus\":1},{\"address\":\"Chennai\",\"deviceDate\":\"2015-08-25T00:00:00\",\"deviceTime\":\" 5:44:03 PM\",\"speed\":54.03,\"vehicleStatus\":1},{\"address\":\"Chennai\",\"deviceDate\":\"2015-08-25T00:00:00\",\"deviceTime\":\" 6:04:04 PM\",\"speed\":56.23,\"vehicleStatus\":1},{\"address\":\"Chennai\",\"deviceDate\":\"2015-08-25T00:00:00\",\"deviceTime\":\" 6:24:04 PM\",\"speed\":62.93,\"vehicleStatus\":1},{\"address\":\"Chennai\",\"deviceDate\":\"2015-08-25T00:00:00\",\"deviceTime\":\" 6:44:09 PM\",\"speed\":62.96,\"vehicleStatus\":1},{\"address\":\"Chennai\",\"deviceDate\":\"2015-08-25T00:00:00\",\"deviceTime\":\" 7:03:52 PM\",\"speed\":71.92,\"vehicleStatus\":1},{\"address\":\"Chennai\",\"deviceDate\":\"2015-08-25T00:00:00\",\"deviceTime\":\" 7:23:00 PM\",\"speed\":65.19,\"vehicleStatus\":1},{\"address\":\"Chennai\",\"deviceDate\":\"2015-08-25T00:00:00\",\"deviceTime\":\" 7:44:42 PM\",\"speed\":56.23,\"vehicleStatus\":1},{\"address\":\"Chennai\",\"deviceDate\":\"2015-08-25T00:00:00\",\"deviceTime\":\" 8:05:40 PM\",\"speed\":78.48,\"vehicleStatus\":1},{\"address\":\"Chennai\",\"deviceDate\":\"2015-08-25T00:00:00\",\"deviceTime\":\" 8:26:12 PM\",\"speed\":67.82,\"vehicleStatus\":1},{\"address\":\"Chennai\",\"deviceDate\":\"2015-08-25T00:00:00\",\"deviceTime\":\" 8:45:13 PM\",\"speed\":60.00,\"vehicleStatus\":1},{\"address\":\"Chennai\",\"deviceDate\":\"2015-08-25T00:00:00\",\"deviceTime\":\" 9:03:41 PM\",\"speed\":60.00,\"vehicleStatus\":1},{\"address\":\"Chennai\",\"deviceDate\":\"2015-08-25T00:00:00\",\"deviceTime\":\" 9:22:21 PM\",\"speed\":70.00,\"vehicleStatus\":1},{\"address\":\"Chennai\",\"deviceDate\":\"2015-08-25T00:00:00\",\"deviceTime\":\" 9:41:06 PM\",\"speed\":60.00,\"vehicleStatus\":1},{\"address\":\"Chennai\",\"deviceDate\":\"2015-08-25T00:00:00\",\"deviceTime\":\"10:00:53 PM\",\"speed\":60.00,\"vehicleStatus\":1},{\"address\":\"Chennai\",\"deviceDate\":\"2015-08-25T00:00:00\",\"deviceTime\":\"10:19:55 PM\",\"speed\":70.00,\"vehicleStatus\":1},{\"address\":\"Chennai\",\"deviceDate\":\"2015-08-25T00:00:00\",\"deviceTime\":\"10:39:54 PM\",\"speed\":51.76,\"vehicleStatus\":1},{\"address\":\"Chennai\",\"deviceDate\":\"2015-08-25T00:00:00\",\"deviceTime\":\"10:58:57 PM\",\"speed\":41.76,\"vehicleStatus\":1},{\"address\":\"Chennai\",\"deviceDate\":\"2015-08-25T00:00:00\",\"deviceTime\":\"11:18:52 PM\",\"speed\":42.33,\"vehicleStatus\":1},{\"address\":\"Chennai\",\"deviceDate\":\"2015-08-25T00:00:00\",\"deviceTime\":\"11:37:58 PM\",\"speed\":42.87,\"vehicleStatus\":1},{\"address\":\"Chennai\",\"deviceDate\":\"2015-08-25T00:00:00\",\"deviceTime\":\"11:56:58 PM\",\"speed\":51.78,\"vehicleStatus\":1},{\"address\":\"Chennai\",\"deviceDate\":\"2015-08-26T00:00:00\",\"deviceTime\":\"12:17:17 AM\",\"speed\":52.84,\"vehicleStatus\":1},{\"address\":\"Chennai\",\"deviceDate\":\"2015-08-26T00:00:00\",\"deviceTime\":\"12:36:23 AM\",\"speed\":51.58,\"vehicleStatus\":1}]";
                break;
            case 41 : //SaveSignature
                offlineData = "{\"status\":\"inserted\"}";
                break;
//            case 42 : //not found
//                break;
            case 42: //Tracking vehicle trip
                offlineData = "[{\\\"status\\\":\\\"OK\\\"},{\\\"latitude\\\":\\\"13.000334\\\",\\\"longitude\\\":\\\" 77.563739\\\"},{\\\"latitude\\\":\\\"13.000156\\\",\\\"longitude\\\":\\\"77.563814\\\"},{\\\"latitude\\\":\\\"12.999926\\\",\\\"longitude\\\":\\\"77.563932\\\"},{\\\"latitude\\\":\\\"12.999549\\\",\\\"longitude\\\":\\\"77.564146\\\"},{\\\"latitude\\\":\\\"12.999267\\\",\\\"longitude\\\":\\\"77.564307\\\"},{\\\"latitude\\\":\\\"12.999100\\\",\\\"longitude\\\":\\\"77.564415\\\"},{\\\"latitude\\\":\\\"12.998828\\\",\\\"longitude\\\":\\\"77.564608\\\"},{\\\"latitude\\\":\\\"12.998556\\\",\\\"longitude\\\":\\\"77.564812\\\"},{\\\"latitude\\\":\\\"12.998222\\\",\\\"longitude\\\":\\\"77.565069\\\"},{\\\"latitude\\\":\\\"12.997521\\\",\\\"longitude\\\":\\\"77.565552\\\"},{\\\"latitude\\\":\\\"12.996675\\\",\\\"longitude\\\":\\\"77.566153\\\"},{\\\"latitude\\\":\\\"12.996455\\\",\\\"longitude\\\":\\\"77.566324\\\"},{\\\"latitude\\\":\\\"12.996215\\\",\\\"longitude\\\":\\\"77.566485\\\"},{\\\"latitude\\\":\\\"12.996016\\\",\\\"longitude\\\":\\\"77.566635\\\"},{\\\"latitude\\\":\\\"12.995880\\\",\\\"longitude\\\":\\\"77.566743\\\"},{\\\"latitude\\\":\\\"12.994908\\\",\\\"longitude\\\":\\\"77.567365\\\"}]";
                break;
            case 43:
                offlineData ="[{\"status\":\"OK\"},{\"vehicleNumber\":\"TN 63 AC 1643 \",\"tripDate\":\"2015-08-25T00:00:00\",\"driverName\":\"RAM\",\"tripVoucher\":\"20150825 035144\"},{\"vehicleNumber\":\"TN 63 AC 1644\",\"tripDate\":\"2015-08-25T00:00:00\",\"driverName\":\"MANISH\",\"tripVoucher\":\"20150825 035326\"},{\"vehicleNumber\":\"TN 63 AC 1645\",\"tripDate\":\"2015-08-25T00:00:00\",\"driverName\":\"MADAN\",\"tripVoucher\":\"20150825 035505\"}]";
                break;
            case 44:
                offlineData="[{\"status\":\"OK\"},{\"sourceName\":\"Devasandra\",\"voucherNumber\":\"20150825 031446\",\"cleaner\":\"DAMAN\",\"driver\":\"RAM\"},[{\"vehicleNumber\":\"TN 63 AC 1643 \",\"tripOrder\":1,\"sourceName\":\"Devasandra\",\"destinationName\":\"Rmv hospital\",\"distance\":40.0,\"product\":\"Petrol\",\"quantity\":200.0,\"paymentType\":\"Commission\",\"paymentKm\":8.00,\"amount\":2000.00,\"subVoucher\":\"SubTrip20150825 031357\",\"route\":\"mahabazar \",\"tripStatus\":1},{\"vehicleNumber\":\"TN 63 AC 1643 \",\"tripOrder\":2,\"sourceName\":\"Rmv hospital\",\"destinationName\":\"Sadashivnagar\",\"distance\":50.0,\"product\":\"Diecel\",\"quantity\":400.0,\"paymentType\":\"Commission\",\"paymentKm\":8.00,\"amount\":3000.00,\"subVoucher\":\"SubTrip20150825 031444\",\"route\":\"Iti\",\"tripStatus\":2}]]";
                break;
            case 45:
                offlineData="{\"status\":\"off trip\"}";
                break;
            case 46:
                offlineData="[{\"status\":\"OK\"},{\"drivers\":[\"RAM\",\"MANISH\",\"MADAN\"]}]";
                break;
            case 47:
                offlineData="[{\"status\":\"OK\"},{\"vehicles\":[\"TN 63 AC 1643 \",\"TN 63 AC 1644\",\"TN 63 AC 1645\"]}]";
                break;
            case 48:
                offlineData="{\"status\":\"updated\"}";
                break;
            case 49:
                offlineData="[{\"status\":\"OK\"},{\"dateTime\":\"2015-08-18T11:17:06\",\"fuel\":250.0},{\"dateTime\":\"2015-08-18T11:21:27\",\"fuel\":245.0},{\"dateTime\":\"2015-08-18T11:27:35\",\"fuel\":240.0},{\"dateTime\":\"2015-08-18T11:30:33\",\"fuel\":235.0},{\"dateTime\":\"2015-08-18T11:46:37\",\"fuel\":230.0},{\"dateTime\":\"2015-08-18T11:53:56\",\"fuel\":5.0},{\"dateTime\":\"2015-08-18T11:56:16\",\"fuel\":240.0},{\"dateTime\":\"2015-08-18T11:58:34\",\"fuel\":5.0},{\"dateTime\":\"2015-08-18T12:00:57\",\"fuel\":225.0},{\"dateTime\":\"2015-08-18T12:03:52\",\"fuel\":220.0},{\"dateTime\":\"2015-08-18T12:06:02\",\"fuel\":250.0},{\"dateTime\":\"2015-08-18T12:12:25\",\"fuel\":245.0},{\"dateTime\":\"2015-08-18T12:15:26\",\"fuel\":240.0},{\"dateTime\":\"2015-08-18T12:16:52\",\"fuel\":235.0},{\"dateTime\":\"2015-08-18T12:19:04\",\"fuel\":230.0},{\"dateTime\":\"2015-08-18T12:21:13\",\"fuel\":225.0},{\"dateTime\":\"2015-08-18T12:24:07\",\"fuel\":220.0},{\"dateTime\":\"2015-08-18T12:29:08\",\"fuel\":215.0}]";
                break;
            case 50:
                offlineData="[{\"status\":\"ok\"},[{\"imei\":\"355255042048505\",\"phone\":\"7022977192\",\"protocol\":\"cyn-vt2\"},{\"imei\":\"005255042048506\",\"phone\":\"7022977100\",\"protocol\":\"cyn-vt2\"},{\"imei\":\"005255042048507\",\"phone\":\"7022977111\",\"protocol\":\"cyn-vt2\"},{\"imei\":\"005255042048508\",\"phone\":\"7022977222\",\"protocol\":\"cyn-vt2\"}]]";
                break;
            default:
                offlineData = "Error in offline webservice";
                break;
        }
        return "{\"d\":" + offlineData + "}";
    }
}
