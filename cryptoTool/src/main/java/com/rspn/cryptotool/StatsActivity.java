package com.rspn.cryptotool;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager.LayoutParams;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rspn.cryptotool.breakencryption.VigenereBreaker;
import com.rspn.cryptotool.model.StatsResults;
import com.rspn.cryptotool.utils.CTUtils;
import com.rspn.cryptotool.utils.CTUtils.EType;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

public class StatsActivity extends ActionBarActivity {

    private LinearLayout characterCount_layout;
    private LinearLayout indexOfCoincidence_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_stats);
        characterCount_layout = (LinearLayout) findViewById(R.id.charcount_layout);
        indexOfCoincidence_layout = (LinearLayout) findViewById(R.id.indexofcoincidence_layout);
        StatsResults statsResults = getIntent().getParcelableExtra("data");

        createBarChart(statsResults);

        //Ads
        AdView adView = (AdView) this.findViewById(R.id.adView_InStatsActivity);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Respond to the action bar's Up/Home button
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        if (id == R.id.action_aboutCharCount) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("About Character Count")
                    .setIcon(R.drawable.ic_info)
                    .setMessage("A text encrypted with Caesar's cypher will maintain a character count proportional to the count in " +
                            "it's original plaintext; therefore this is an example of monoalphabetic encryption. \n" +
                            "On the other hand Viginere cypher is a polyaphabetic encryption. That is, a plaintext character can be encrypted " +
                            "with more that one corresponding character.")
                    .setCancelable(true)
                    .setPositiveButton("More Info", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://practicalcryptography.com/cryptanalysis/text-characterisation/identifying-unknown-ciphers/"));
                            startActivity(browserIntent);
                        }
                    });
            builder.show();


            return true;
        }

        if (id == R.id.action_aboutIC) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("About Index of Coincidence")
                    .setIcon(R.drawable.ic_info)
                    .setMessage("By estimating the length of the key, it is possible to determine " +
                            "how similar a random distribution is when compared to a standard distribution." +
                            "\n In our case the standard distribution is given by the distribution of English caracters" +
                            "found in regular plaintext. In English the Index of Coincidence is around 0.065")
                    .setCancelable(true)
                    .setPositiveButton("More Info", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://practicalcryptography.com/cryptanalysis/text-characterisation/index-coincidence/"));
                            startActivity(browserIntent);
                        }
                    });
            builder.show();

            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void createBarChart(StatsResults data) {

        int[] charCount = data.getCharCount();

        XYSeries charCountSeries = new XYSeries("Character Count");
        double maxYAxis = 0;
        for (int i = 0; i < charCount.length; i++) {
            charCountSeries.add(i, charCount[i]);
            if (charCount[i] > maxYAxis)
                maxYAxis = charCount[i];
        }

        maxYAxis = (int) (maxYAxis * 1.10);// maxXAxis is 15% more that actual max


        XYMultipleSeriesDataset datasetCharCount = new XYMultipleSeriesDataset();
        datasetCharCount.addSeries(charCountSeries);


        XYSeriesRenderer rendererCharCount = new XYSeriesRenderer();
        rendererCharCount.setColor(Color.rgb(242, 117, 34));
        rendererCharCount.setFillPoints(true);
        rendererCharCount.setLineWidth(2);
        rendererCharCount.setDisplayChartValues(true);

        XYMultipleSeriesRenderer multirendererCharCount = new XYMultipleSeriesRenderer();
        multirendererCharCount.setChartTitle("Character Count in Encrypted Text");
        multirendererCharCount.setXTitle("Characters");
        multirendererCharCount.setYTitle("Count");
        multirendererCharCount.setXLabels(0);
        /*Chart customization*/
        multirendererCharCount.setXLabelsColor(Color.BLACK);
        multirendererCharCount.setLabelsColor(Color.BLACK);
        //setting text size of the title
        multirendererCharCount.setChartTitleTextSize(28);
        //setting text size of the axis title
        multirendererCharCount.setAxisTitleTextSize(28);
        //setting text size of the graph lable
        multirendererCharCount.setLabelsTextSize(28);
        //setting zoom buttons visiblity
        multirendererCharCount.setZoomButtonsVisible(false);
        //setting pan enablity which uses graph to move on both axis
        multirendererCharCount.setPanEnabled(true, false);
        //setting click false on graph
        multirendererCharCount.setClickEnabled(false);
        //setting zoom to false on both axis
        multirendererCharCount.setZoomEnabled(true, false);
        //setting lines to display on y axis
        multirendererCharCount.setShowGridY(false);
        //setting lines to display on x axis
        multirendererCharCount.setShowGridX(false);
        //setting legend to fit the screen size
        multirendererCharCount.setFitLegend(true);
        //setting displaying line on grid
        multirendererCharCount.setShowGrid(false);
        //setting zoom to false
        multirendererCharCount.setZoomEnabled(false);
        //setting external zoom functions to false
        multirendererCharCount.setExternalZoomEnabled(false);
        //setting displaying lines on graph to be formatted(like using graphics)
        multirendererCharCount.setAntialiasing(true);
        //setting to in scroll to false
        multirendererCharCount.setInScroll(false);
        //setting to set legend height of the graph
        multirendererCharCount.setLegendHeight(30);
        //setting x axis label align
        multirendererCharCount.setXLabelsAlign(Align.CENTER);
        //setting y axis label to align
        multirendererCharCount.setYLabelsAlign(Align.LEFT);
        //setting text style
        multirendererCharCount.setTextTypeface("sans_serif", Typeface.NORMAL);
        //setting no of values to display in y axis
        multirendererCharCount.setYLabels(10);
        // setting y axis max value, Since i'm using static values inside the graph so i'm setting y max value to 4000.
        // if you use dynamic values then get the max y value and set here
        multirendererCharCount.setYAxisMax(maxYAxis);
        //setting used to move the graph on xaxiz to .5 to the right
        multirendererCharCount.setXAxisMin(-1.0);
        //setting used to move the graph on yaxiz to .5 up
        multirendererCharCount.setYAxisMin(-0.5);
        //setting max values to be display in x axis
        multirendererCharCount.setXAxisMax(26);
        //setting bar size or space between two bars
        multirendererCharCount.setBarSpacing(0.5);
        //Setting background color of the graph to transparent
        multirendererCharCount.setBackgroundColor(Color.TRANSPARENT);
        //Setting margin color of the graph to transparent
        multirendererCharCount.setMarginsColor(getResources().getColor(R.color.transparent_background));
        multirendererCharCount.setApplyBackgroundColor(true);
        //setting the margin size for the graph in the order top, left, bottom, right
        multirendererCharCount.setMargins(new int[]{30, 30, 30, 30});

        char[] alphabet = CTUtils.getAlphabet();
        for (int i = 0; i < 26; i++) {
            multirendererCharCount.addTextLabel(i, Character.toString(alphabet[i]));
        }

        multirendererCharCount.addSeriesRenderer(rendererCharCount);
        characterCount_layout.removeAllViews();
        View charCountChart = ChartFactory.getBarChartView(this, datasetCharCount, multirendererCharCount, Type.DEFAULT);
        charCountChart.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        characterCount_layout.addView(charCountChart);
        charCountChart.setId(R.id.charCountChart_View);

        if (EType.valueOf(data.getResultType()) == EType.VIGENERE) {
            double[] icValues = data.getICforKeyLength();
            XYSeries icSeries = new XYSeries("Index of Coincidence");
            XYSeries constantICSeries = new XYSeries("English Index of Coincidence");
            maxYAxis = -1;
            for (int i = 0; i < icValues.length; i++) {
                icSeries.add(i, Math.round(icValues[i] * 1000.0) / 1000.0);
                constantICSeries.add(i, CTUtils.EnglishIC);
                if (icValues[i] > maxYAxis)
                    maxYAxis = icValues[i];
            }

            maxYAxis = maxYAxis * 1.10;// maxXAxis is 10% more that actual max

            XYMultipleSeriesDataset datasetICValues = new XYMultipleSeriesDataset();
            datasetICValues.addSeries(constantICSeries);
            datasetICValues.addSeries(icSeries);


            //Constant English IC
            XYSeriesRenderer rendererEnglishIC = new XYSeriesRenderer();
            rendererEnglishIC.setColor(Color.BLUE);
            rendererEnglishIC.setPointStyle(PointStyle.CIRCLE);
            rendererEnglishIC.setFillPoints(true);
            rendererEnglishIC.setLineWidth(2);
            rendererEnglishIC.setDisplayChartValues(true);

            //IC Bar
            XYSeriesRenderer rendererICValues = new XYSeriesRenderer();
            rendererICValues.setColor(Color.rgb(242, 117, 34));
            rendererICValues.setFillPoints(true);
            rendererICValues.setLineWidth(2);
            rendererICValues.setDisplayChartValues(true);

            XYMultipleSeriesRenderer multirendererICValues = new XYMultipleSeriesRenderer();
            multirendererICValues.setChartTitle("Index of Coincidence for Key Lengths");
            multirendererICValues.setXTitle("Key Length");
            multirendererICValues.setYTitle("Index of Coincidence");
            multirendererICValues.setXLabels(0);
            /*Chart customization*/
            multirendererICValues.setXLabelsColor(Color.BLACK);
            multirendererICValues.setLabelsColor(Color.BLACK);
            //setting text size of the title
            multirendererICValues.setChartTitleTextSize(28);
            //setting text size of the axis title
            multirendererICValues.setAxisTitleTextSize(28);
            //setting text size of the graph lable
            multirendererICValues.setLabelsTextSize(28);
            //setting zoom buttons visiblity
            multirendererICValues.setZoomButtonsVisible(false);
            //setting pan enablity which uses graph to move on both axis
            multirendererICValues.setPanEnabled(true, false);
            //setting click false on graph
            multirendererICValues.setClickEnabled(false);
            //setting zoom to false on both axis
            multirendererICValues.setZoomEnabled(true, true);
            //setting lines to display on y axis
            multirendererICValues.setShowGridY(false);
            //setting lines to display on x axis
            multirendererICValues.setShowGridX(false);
            //setting legend to fit the screen size
            multirendererICValues.setFitLegend(true);
            //setting displaying line on grid
            multirendererICValues.setShowGrid(false);
            //setting zoom to false
            multirendererICValues.setZoomEnabled(false);
            //setting external zoom functions to false
            multirendererICValues.setExternalZoomEnabled(false);
            //setting displaying lines on graph to be formatted(like using graphics)
            multirendererICValues.setAntialiasing(true);
            //setting to in scroll to false
            multirendererICValues.setInScroll(false);
            //setting to set legend height of the graph
            multirendererICValues.setLegendHeight(30);
            //setting x axis label align
            multirendererICValues.setXLabelsAlign(Align.CENTER);
            //setting y axis label to align
            multirendererICValues.setYLabelsAlign(Align.LEFT);
            //setting text style
            multirendererICValues.setTextTypeface("sans_serif", Typeface.NORMAL);
            //setting no of values to display in y axis
            multirendererICValues.setYLabels(10);
            // setting y axis max value, Since i'm using static values inside the graph so i'm setting y max value to 4000.
            // if you use dynamic values then get the max y value and set here
            multirendererICValues.setYAxisMax(maxYAxis);
            //setting used to move the graph on xaxiz to .5 to the right
            multirendererICValues.setXAxisMin(-0.5);
            //setting used to move the graph on yaxiz to .5 up
            multirendererICValues.setYAxisMin(-0.0010);
            //setting max values to be display in x axis
            multirendererICValues.setXAxisMax(VigenereBreaker.maxKeyLength);
            //setting bar size or space between two bars
            multirendererICValues.setBarSpacing(0.5);
            //Setting background color of the graph to transparent
            multirendererICValues.setBackgroundColor(Color.TRANSPARENT);
            //Setting margin color of the graph to transparent
            multirendererICValues.setMarginsColor(getResources().getColor(R.color.transparent_background));
            multirendererICValues.setApplyBackgroundColor(true);
            //setting the margin size for the graph in the order top, left, bottom, right
            multirendererICValues.setMargins(new int[]{30, 30, 30, 30});

            for (int i = 1; i <= VigenereBreaker.maxKeyLength; i++) {
                multirendererICValues.addTextLabel(i - 1, Integer.toString(i));
            }

            multirendererICValues.addSeriesRenderer(rendererEnglishIC);
            multirendererICValues.addSeriesRenderer(rendererICValues);

            String[] types = new String[]{LineChart.TYPE, BarChart.TYPE};

            View icValuesChart = ChartFactory.getCombinedXYChartView(this, datasetICValues, multirendererICValues, types);
            icValuesChart.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            indexOfCoincidence_layout.addView(icValuesChart);
            icValuesChart.setId(R.id.icValuesChart_View);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stats, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
