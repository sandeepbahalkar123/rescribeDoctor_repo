package com.rescribe.doctor.util;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.rescribe.doctor.R;
import com.rescribe.doctor.interfaces.CheckIpConnection;
import com.rescribe.doctor.interfaces.DatePickerDialogListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CommonMethods {

    private static final String TAG = "Rescribe/CommonMethods";
    private static boolean encryptionIsOn = true;
    private static String aBuffer = "";
    private static CheckIpConnection mCheckIpConnection;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private DatePickerDialogListener mDatePickerDialogListener;

    public static void showToast(Context context, String error) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
    }



    public static void showSnack(View mViewById, String msg) {
        if (mViewById != null) {
            Snackbar.make(mViewById, msg, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            Log.d(TAG, "null snacbar view" + msg);
        }
    }


    /**
     * Email validator
     *
     * @param emailId
     * @return
     */
    public final static boolean isValidEmail(CharSequence emailId) {
        if (emailId == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(emailId).matches();
        }
    }


    public static void hideKeyboard(Activity cntx) {
        // Check if no view has focus:
        View view = cntx.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) cntx.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static String getCurrentDateTime() // for enrollmentId
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int date = calendar.get(Calendar.DATE);

        String Year = String.valueOf(year);
        StringBuffer dString = new StringBuffer();
        dString.append((date > 9) ? String.valueOf(date) : ("0" + date));
        dString.append("-");
        dString.append((month > 9) ? String.valueOf(month) : ("0" + month));
        dString.append("-");
        dString.append(year);
        return dString.toString();
    }

    public static String getCurrentDate() // for enrollmentId
    {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(RescribeConstants.DD_MM_YYYY, Locale.US);
        return df.format(c.getTime());
    }

    public static void showSnack(Context mContext, View mViewById, String msg) {
        Snackbar snack = Snackbar.make(mViewById, msg, Snackbar.LENGTH_SHORT);
        ViewGroup group = (ViewGroup) snack.getView();
        group.setBackgroundColor(mContext.getResources().getColor(R.color.errorColor));
        snack.show();
    }


    public static String getCurrentTimeStamp(String expectedFormat) {
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat(expectedFormat, Locale.US);
            String currentTimeStamp = dateFormat.format(new Date()); // Find todays date

            return currentTimeStamp;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }


    public static void dateDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays,
                elapsedHours, elapsedMinutes, elapsedSeconds);

    }


    public static String getCalculatedDate(String inFormat, int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, days);
        Date date = new Date(cal.getTimeInMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(inFormat, Locale.US);
        return dateFormat.format(date);
    }

    public static String getDayFromDate(String dateFormat, String date) {

        date = date.trim();
        Date currentDate = new Date();
        String timeString = new SimpleDateFormat(dateFormat + " HH:mm:ss", Locale.US).format(currentDate).substring(10);

        SimpleDateFormat mainDateFormat = new SimpleDateFormat(dateFormat + " HH:mm:ss", Locale.US);
        Date formattedInputDate = null;
        try {
            formattedInputDate = mainDateFormat.parse(date + timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date.trim().equalsIgnoreCase(new SimpleDateFormat(dateFormat, Locale.US).format(currentDate).trim())) {
            return "Today";
        }
        //-----------
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date yesterdayDate = cal.getTime();
        String sDate = new SimpleDateFormat(dateFormat, Locale.US).format(yesterdayDate);
        try {
            yesterdayDate = mainDateFormat.parse(sDate + timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (formattedInputDate.getTime() == yesterdayDate.getTime()) {
            return "Yesterday";
        }
        //-----------
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, +1);
        Date tomorrowDate = cal.getTime();
        sDate = new SimpleDateFormat(dateFormat, Locale.US).format(tomorrowDate);
        try {
            tomorrowDate = mainDateFormat.parse(sDate + timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (formattedInputDate.getTime() == tomorrowDate.getTime()) {
            return "Tomorrow";
        } else {
            DateFormat f = new SimpleDateFormat("EEEE", Locale.US);
            try {
                return f.format(formattedInputDate);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }

    public static void Log(String tag, String message) {
        Log.e(tag, "" + message);
    }


    public static String getFormatedDate(String strDate, String sourceFormate,
                                         String destinyFormate) {
        SimpleDateFormat df;
        df = new SimpleDateFormat(sourceFormate, Locale.US);
        Date date = null;
        try {
            date = df.parse(strDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        df = new SimpleDateFormat(destinyFormate, Locale.US);
        return df.format(date);
    }

    /**
     * The method will return the date and time in requested format
     *
     * @param selectedDateTime to be converted to requested format
     * @param requestedFormat  the format in which the provided datetime needs to be changed
     * @param formatString     differentiate parameter to format date or time
     * @return formated date or time
     */
    public static String formatDateTime(String selectedDateTime, String requestedFormat, String currentDateFormat, String formatString) {


        if (formatString.equalsIgnoreCase(RescribeConstants.TIME)) {
            // SimpleDateFormat ft = new SimpleDateFormat(RescribeConstants.DATE_PATTERN.HH_MM, Locale.US);
            SimpleDateFormat ft = new SimpleDateFormat(currentDateFormat, Locale.US);

            Date dateObj = null;

            try {
                dateObj = ft.parse(selectedDateTime);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            long millis = dateObj.getTime();
            SimpleDateFormat simpleDateFormatObj = new SimpleDateFormat(requestedFormat, Locale.US);
            return simpleDateFormatObj.format(millis);

        }//if

        else if (formatString.equalsIgnoreCase(RescribeConstants.DATE)) {
            SimpleDateFormat ft = new SimpleDateFormat(currentDateFormat, Locale.US);

            Date dateObj = null;

            try {
                dateObj = ft.parse(selectedDateTime);

            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            SimpleDateFormat simpleDateFormatObj = new SimpleDateFormat(requestedFormat, Locale.US);
            return simpleDateFormatObj.format(dateObj);


        }
        return null;

    }


    public static int convertDpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }


    public static void showInfoDialog(String msg, final Context mContext, final boolean closeActivity) {

        final Dialog dialog = new Dialog(mContext);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_ok);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        ((TextView) dialog.findViewById(R.id.textview_sucess)).setText(msg);

        dialog.findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (closeActivity)
                    ((AppCompatActivity) mContext).finish();
            }
        });

        dialog.show();
    }

    public static String getMealTime(int hour, int mint, Context context) {
        //BB : 7-11,lunch : 11-3,dinner :7-11
        String time = "";
        if (hour > 7 && hour < 11)
            time = context.getString(R.string.break_fast);
        else if (hour >= 11 && hour < 15)
            time = context.getString(R.string.mlunch);
        else if (hour >= 15 && hour <= 17)
            time = context.getString(R.string.msnacks);
        else if (hour >= 17 && hour <= 24)
            time = context.getString(R.string.mdinner);

        CommonMethods.Log(TAG, "hour" + hour);
        CommonMethods.Log(TAG, "getMealTime" + time);
        return time;
    }

    public static Drawable getMedicalTypeIcon(String medicineTypeName, Context context) {

        Drawable abbreviation = ContextCompat.getDrawable(context, R.mipmap.highlight);
        if (medicineTypeName.equalsIgnoreCase("syrup"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.syrup_01);
        else if (medicineTypeName.equalsIgnoreCase("Tablet"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.tablet_02);
        else if (medicineTypeName.equalsIgnoreCase("Capsule"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.capsule_01);
        else if (medicineTypeName.equalsIgnoreCase("injection"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.injection_01);
        else if (medicineTypeName.equalsIgnoreCase("insulin"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.insulin_01);
        else if (medicineTypeName.equalsIgnoreCase("Inhaler"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.inhaler_01);
        else if (medicineTypeName.equalsIgnoreCase("liquid"))
            abbreviation = ContextCompat.getDrawable(context, R.mipmap.highlight); // not found
        else if (medicineTypeName.equalsIgnoreCase("tan"))
            abbreviation = ContextCompat.getDrawable(context, R.mipmap.highlight);// not found
        else if (medicineTypeName.equalsIgnoreCase("cream"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.cream_01);
        else if (medicineTypeName.equalsIgnoreCase("jelly"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.jelly_02);
        else if (medicineTypeName.equalsIgnoreCase("local application"))
            abbreviation = ContextCompat.getDrawable(context, R.mipmap.highlight);// not found
        else if (medicineTypeName.equalsIgnoreCase("ointment"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.ointment_01);
        else if (medicineTypeName.equalsIgnoreCase("lotion"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.lotion_01);
        else if (medicineTypeName.equalsIgnoreCase("drops"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.drop_01);
        else if (medicineTypeName.equalsIgnoreCase("eye drops"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.eye_drops_02);
        else if (medicineTypeName.equalsIgnoreCase("nasal drops"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.nasal_drop_01_01);
        else if (medicineTypeName.equalsIgnoreCase("nasal spray"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.nasal_spray_01);
        else if (medicineTypeName.equalsIgnoreCase("ointment/powder"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.ointment_powder_01);
        else if (medicineTypeName.equalsIgnoreCase("respules"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.respules_01);
        else if (medicineTypeName.equalsIgnoreCase("rotacaps"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.rotacaps_01);
        else if (medicineTypeName.equalsIgnoreCase("sachet"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.sachet_01);

        return abbreviation;
    }

    // Return medicine Icon's

    public static Drawable getMedicineTypeImage(String medicineTypeName, Context context) {

        Drawable abbreviation = ContextCompat.getDrawable(context, R.mipmap.highlight);
        if (medicineTypeName.equalsIgnoreCase("syrup"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.syrup);
        else if (medicineTypeName.equalsIgnoreCase("Tablet"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.tablet);
        else if (medicineTypeName.equalsIgnoreCase("Capsule"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.capsule);
        else if (medicineTypeName.equalsIgnoreCase("injection"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.injection);
        else if (medicineTypeName.equalsIgnoreCase("insulin"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.insulin);
        else if (medicineTypeName.equalsIgnoreCase("Inhaler"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.inhaler);
        else if (medicineTypeName.equalsIgnoreCase("liquid"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.liquid); // not found
        else if (medicineTypeName.equalsIgnoreCase("tan"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.tan);// not found
        else if (medicineTypeName.equalsIgnoreCase("cream"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.cream);
        else if (medicineTypeName.equalsIgnoreCase("jelly"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.jelly);
        else if (medicineTypeName.equalsIgnoreCase("local application"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.tablet);// not found
        else if (medicineTypeName.equalsIgnoreCase("ointment"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.ointment);
        else if (medicineTypeName.equalsIgnoreCase("lotion"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.lotion);
        else if (medicineTypeName.equalsIgnoreCase("drops"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.drop);
        else if (medicineTypeName.equalsIgnoreCase("eye drops"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.eye_drops);
        else if (medicineTypeName.equalsIgnoreCase("nasal drops"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.nasal_drop);
        else if (medicineTypeName.equalsIgnoreCase("nasal spray"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.nasal_spray);
        else if (medicineTypeName.equalsIgnoreCase("ointment/powder"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.ointment_powder);
        else if (medicineTypeName.equalsIgnoreCase("respules"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.respules);
        else if (medicineTypeName.equalsIgnoreCase("rotacaps"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.rotocaps);
        else if (medicineTypeName.equalsIgnoreCase("sachet"))
            abbreviation = ContextCompat.getDrawable(context, R.drawable.sachet);

        return abbreviation;
    }

    public static int getCaseStudyIcons(String caseStudyName) {

        // Drawable abbreviation = ContextCompat.getDrawable(context, R.drawable.ellipse_2);
        int abbreviation = R.drawable.common_icon;
        if (caseStudyName.equalsIgnoreCase("complaints"))
            abbreviation = R.drawable.complaints;
        else if (caseStudyName.equalsIgnoreCase("vitals"))
            abbreviation = R.drawable.vitals;
        else if (caseStudyName.equalsIgnoreCase("remarks"))
            abbreviation = R.drawable.remarks;
        else if (caseStudyName.equalsIgnoreCase("diagnosis"))
            abbreviation = R.drawable.diagnosis;
        else if (caseStudyName.equalsIgnoreCase("prescription"))
            abbreviation = R.drawable.prescription;
        else if (caseStudyName.equalsIgnoreCase("investigations"))
            abbreviation = R.drawable.investigations;
        else if (caseStudyName.equalsIgnoreCase("advice"))
            abbreviation = R.drawable.advice; // not found
        else if (caseStudyName.equalsIgnoreCase("treatmentplan"))
            abbreviation = R.drawable.treatment_plan; // not found
        else if (caseStudyName.equalsIgnoreCase("surgery"))
            abbreviation = R.drawable.surgery; // not found
        else if (caseStudyName.equalsIgnoreCase("vaccination"))
            abbreviation = R.drawable.vaccination; // not found
        else if (caseStudyName.equalsIgnoreCase("generalprecautions"))
            abbreviation = R.drawable.general_precautions; // not found
        else if (caseStudyName.equalsIgnoreCase("preoperativeprecautions"))
            abbreviation = R.drawable.pre_operative_precautions; // not found
        else if (caseStudyName.equalsIgnoreCase("postoperativecare"))
            abbreviation = R.drawable.post_operative_care; // not found
        else if (caseStudyName.equalsIgnoreCase("painscore"))
            abbreviation = R.drawable.pain_score; // not found
        else if (caseStudyName.equalsIgnoreCase("exercise"))
            abbreviation = R.drawable.exercise; // not found
        else if (caseStudyName.equalsIgnoreCase("finding"))
            abbreviation = R.drawable.finding; // not found
        else if (caseStudyName.equalsIgnoreCase("allergy"))
            abbreviation = R.drawable.allergy; // not found

        return abbreviation;
    }

    public static int getVitalIcons(String vitalDetailName) {

        // Drawable abbreviation = ContextCompat.getDrawable(context, R.drawable.ellipse_2);
        int abbreviation = R.drawable.ellipse_2;
        if (vitalDetailName.equalsIgnoreCase("bp"))
            abbreviation = R.drawable.bp;
        else if (vitalDetailName.equalsIgnoreCase("weight"))
            abbreviation = R.drawable.weight;
        else if (vitalDetailName.equalsIgnoreCase("height"))
            abbreviation = R.drawable.height;
        else if (vitalDetailName.equalsIgnoreCase("bmi"))
            abbreviation = R.drawable.bmi;
        else if (vitalDetailName.equalsIgnoreCase("totalcholesterolhdlcholesterol"))
            abbreviation = R.drawable.totalcholesterolhdlcholesterol;
        else if (vitalDetailName.equalsIgnoreCase("ldlhdl"))
            abbreviation = R.drawable.ldlhdl;
        else if (vitalDetailName.equalsIgnoreCase("triglycerides"))
            abbreviation = R.drawable.triglycerides;
        else if (vitalDetailName.equalsIgnoreCase("hdlcholesterol"))
            abbreviation = R.drawable.hdlcholesterol;
        else if (vitalDetailName.equalsIgnoreCase("ldlcholesterol"))
            abbreviation = R.drawable.ldlcholesterol;
        else if (vitalDetailName.equalsIgnoreCase("totalcholesterol"))
            abbreviation = R.drawable.totalcholesterol;
        else if (vitalDetailName.equalsIgnoreCase("gfr"))
            abbreviation = R.drawable.gfr;
        else if (vitalDetailName.equalsIgnoreCase("bun"))
            abbreviation = R.drawable.bun;
        else if (vitalDetailName.equalsIgnoreCase("creatinine"))
            abbreviation = R.drawable.creatinine;
        else if (vitalDetailName.equalsIgnoreCase("respiratoryrate"))
            abbreviation = R.drawable.respiratoryrate;
        else if (vitalDetailName.equalsIgnoreCase("heartrate"))
            abbreviation = R.drawable.heartrate;
        else if (vitalDetailName.equalsIgnoreCase("temperature"))
            abbreviation = R.drawable.temperature;
        else if (vitalDetailName.equalsIgnoreCase("fbs"))
            abbreviation = R.drawable.fbs;
        else if (vitalDetailName.equalsIgnoreCase("ppbs"))
            abbreviation = R.drawable.ppbs;
        else if (vitalDetailName.equalsIgnoreCase("spo_2"))
            abbreviation = R.drawable.spo_2;
        else if (vitalDetailName.equalsIgnoreCase("platelet"))
            abbreviation = R.drawable.platelet;
        else if (vitalDetailName.equalsIgnoreCase("esr"))
            abbreviation = R.drawable.esr;
        else if (vitalDetailName.equalsIgnoreCase("hb"))
            abbreviation = R.drawable.hb;

        return abbreviation;
    }


    public static Date convertStringToDate(String dateString, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.US);
        Date date = null;

        try {
            date = formatter.parse(dateString.trim());
        } catch (ParseException e) {
            e.printStackTrace();
            CommonMethods.Log("convertStringToDate", "convertStringToDate EXCEPTION OCCURS : " + e.getMessage());
        }
        return date;
    }

    public static String getDateSelectedDoctorVisit(String visitdate, String dateFormat) {
        String yourDate = null;

        DateFormat format = new SimpleDateFormat(dateFormat, Locale.US);

        try {
            Date date = format.parse(visitdate);
            format = new SimpleDateFormat("d'th' MMM, yyyy", Locale.US);
            yourDate = format.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return yourDate;

    }


    public static String getSuffixForNumber(final int n) {
        //  checkArgument(n >= 1 && n <= 31, "illegal day of month: " + n);
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }

    }

    //TODO : this is done for temp
    public static ArrayList<String> getYearForDoctorList() {
        ArrayList<String> a = new ArrayList<>();
        a.add("2017");
        return a;
    }

    public void datePickerDialog(Context context, DatePickerDialogListener datePickerDialogListener, Date dateToSet, final Boolean isFromDateClicked, final Date date) {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        if (dateToSet != null) {
            c.setTime(dateToSet);
        }
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        mDatePickerDialogListener = datePickerDialogListener;

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {


                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        if (isFromDateClicked) {
                            mDatePickerDialogListener.getSelectedDate(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        } else {
                            mDatePickerDialogListener.getSelectedDate(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        }


                    }
                }, mYear, mMonth, mDay);
        if (isFromDateClicked) {
            datePickerDialog.getDatePicker().setCalendarViewShown(false);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        } else {
            if (date != null) {
                datePickerDialog.getDatePicker().setCalendarViewShown(false);
                datePickerDialog.getDatePicker().setMinDate(date.getTime());
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            } else {
                datePickerDialog.getDatePicker().setCalendarViewShown(false);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        }
    }
}

