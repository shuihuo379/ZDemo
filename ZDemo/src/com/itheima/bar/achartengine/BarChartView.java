package com.itheima.bar.achartengine;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.View;

public class BarChartView{
	private XYMultipleSeriesRenderer renderer;
	private Context context;
	private String[] xText = new String[]{"05-01","05-02","05-03","05-04",
			"05-05","05-06","05-07"};
	private double[] d1 = new double[] {2000,3000,1500,1500,1000,4500,2000};
	private double[] d2 = new double[] {2500,2500,3500,500,2000,4000,2500};
	private int[] colors = new int[] { Color.BLUE, Color.CYAN};
	private String[] titles = new String[] { "个人", "他人" };
	private List<double[]> valueList;
	
	public BarChartView(Context context){
		this.context = context;
		renderer = new XYMultipleSeriesRenderer();
		valueList = new ArrayList<double[]>();
		valueList.add(d1);
		valueList.add(d2);  
	}
	
	public View getBarChartView(String yTitle,double yMax){
		ChartSettings(yTitle,yMax);
//		renderer.getSeriesRendererAt(0).setDisplayChartValues(false); //设置柱子上是否显示数量值
		View view = ChartFactory.getBarChartView(context,getDataSet(), renderer, Type.DEFAULT); // Type.STACKED
		return view;
	}
	
	/**
	* 构造数据
	* @return
	*/
	public XYMultipleSeriesDataset getDataSet() {
		// 构造数据
		XYMultipleSeriesDataset barDataset = new XYMultipleSeriesDataset();
		for(int i=0;i<titles.length;i++){
			 XYSeries series = new XYSeries(titles[i]); //图例的标题
			 double [] yLable= valueList.get(i); //size=2
			 for(int j=0;j<yLable.length;j++){ 
				 series.add(j+1,yLable[j]);
			 }
			 barDataset.addSeries(series);
			 XYSeriesRenderer xyRenderer = new XYSeriesRenderer();
			 xyRenderer.setDisplayChartValues(false);   //设置柱子上是否显示数量值,默认是false
		     xyRenderer.setColor(colors[i]);  // 设置颜色
		     xyRenderer.setPointStyle(PointStyle.SQUARE); // 设置点的样式 
		     xyRenderer.setFillPoints(true);
		     renderer.addSeriesRenderer(xyRenderer);  // 将要绘制的点添加到坐标绘制中
		}
		return barDataset;
	}

	private void ChartSettings(String yTitle,double yMax) {
//		renderer.setXTitle("日期");
//		renderer.setYTitle(yTitle);
//		renderer.setChartTitle("个人收支表");  //设置柱图名称
		renderer.setAxesColor(Color.DKGRAY);  //设置 XY 轴颜色
		renderer.setLabelsColor(Color.BLACK);  // 设置轴标签颜色
		renderer.setXAxisMin(0);
		renderer.setXAxisMax(8.5);
		renderer.setYAxisMin(0.5);
		renderer.setYAxisMax(yMax);  // 设置X,Y轴的最小数字和最大数字
		renderer.setLabelsTextSize(25);  //设置轴标签字体大小
		renderer.setLegendTextSize(25); // 设置图例字体大小
		renderer.setMargins(new int[]{10,60,10,0}); //上,左,下,右(控制你图的边距  实现跟图例的分离)
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setXLabelsAlign(Align.CENTER);
		renderer.setBarSpacing(0.8f);  // 柱子间宽度 
		renderer.setZoomButtonsVisible(false); 
		renderer.setAntialiasing(true);  // 消除锯齿
		renderer.setPanEnabled(false, false);   // 设置移动
		renderer.setZoomEnabled(true,true);  // 设置放大  
		renderer.setZoomRate(1.5f);
		renderer.setXLabels(0); //设置X轴显示的刻度标签的个数
		renderer.setYLabels(6); //设置Y轴显示的刻度标签的个数
		renderer.setXLabelsPadding(200); //设置标签的间距
		renderer.setShowAxes(true); //设置是否需要显示坐标轴
		renderer.addXTextLabel(0, String.valueOf(0)); //设置显示X轴起始坐标为0
		renderer.setFitLegend(true);// 调整合适的位置
		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(Color.WHITE);
		renderer.setMarginsColor(Color.WHITE);  //设置周边背景色为白色
		
		for(int i=0;i<xText.length;i++){
			renderer.addXTextLabel(i+1,xText[i]);  //替换X轴内容
		}
		
		
		 // 初次设置每条柱子的颜色  
//		SimpleSeriesRenderer sr = new SimpleSeriesRenderer();  
//		sr.setColor(Color.rgb(157,206,10));
//		renderer.addSeriesRenderer(sr);
	}
}
