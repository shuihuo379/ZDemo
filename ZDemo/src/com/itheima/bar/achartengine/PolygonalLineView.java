package com.itheima.bar.achartengine;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.View;

/**
 * 折线图
 * @author Administrator
 */
public class PolygonalLineView {
	private Context context;
	private XYMultipleSeriesRenderer renderer;
	private double[] y1 = new double[] {2000,3000,1500,1500,1000,4500,2000};
	private String[] xText = new String[]{"05-01","05-07"};
	
	public PolygonalLineView(Context context) {
		this.context = context; 
		renderer = new XYMultipleSeriesRenderer();
	}
	
	public View getPolygonalLineView(){
		PolygonalChartSetting();
		View view = ChartFactory.getLineChartView(context,getDataSet(),renderer);
		return view;
	}
	
	private void PolygonalChartSetting() {
		renderer.setChartTitleTextSize(0);  //设置图表标题字体大小,设置0是把标题隐藏掉 
		renderer.setAxesColor(Color.DKGRAY); // 设置 XY 轴颜色
		renderer.setLabelsColor(Color.BLACK); // 设置轴标签颜色
		renderer.setLabelsTextSize(25); // 设置轴标签字体大小
		renderer.setLegendTextSize(0); // 设置图例字体大小
		renderer.setMargins(new int[] { 30, 60, 0, 10 }); // 上,左,下,右(控制你图的边距,实现跟图例的分离)
		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(Color.WHITE);  //设置中间背景色 
		renderer.setMarginsColor(Color.WHITE); // 设置周边背景色为白色
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setXLabelsAlign(Align.CENTER);
		renderer.setXAxisMin(0.25f);
		renderer.setXAxisMax(7.5);
		renderer.setYAxisMin(0);
		renderer.setYAxisMax(5000); 
		renderer.setXLabels(0); // 设置X轴显示的刻度标签的个数
		renderer.setYLabels(6); // 设置Y轴显示的刻度标签的个数
		renderer.setPointSize(10f);  //设置点的大小
		
		renderer.addXTextLabel(1,xText[0]);
		renderer.addXTextLabel(7,xText[1]);  //首尾添加标签
	}

	/**
	* 构造数据
	* @return
	*/
	public XYMultipleSeriesDataset getDataSet() {
		// 构造数据
		XYMultipleSeriesDataset barDataset = new XYMultipleSeriesDataset();
		XYSeries series = new XYSeries(""); //图例的标题
		for(int i=0;i<y1.length;i++){
			series.add(i+1,y1[i]);
		}
		barDataset.addSeries(series);
		
		XYSeriesRenderer xyRenderer = new XYSeriesRenderer();
        xyRenderer.setColor(Color.RED); //设置颜色
        xyRenderer.setPointStyle(PointStyle.CIRCLE); //设置点的样式
        xyRenderer.setFillPoints(true); //设置图上的点为实心
        xyRenderer.setLineWidth(3f);
        renderer.addSeriesRenderer(xyRenderer); //将要绘制的点添加到坐标绘制中
		
		return barDataset;
	}
}
