package com.xuwei.application.gaodemapdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.route.DistanceItem;
import com.amap.api.services.route.DistanceResult;
import com.amap.api.services.route.DistanceSearch;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DistanceSearch.OnDistanceSearchListener, GeocodeSearch.OnGeocodeSearchListener {
    GeocodeSearch geocodeSearch;
    List<Double> Latitem=new ArrayList<>();   //纬度
    List<Double> Lonitem=new ArrayList<>();   //经度
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * 计算两地距离的函数
         * @param city1:第一个城市
         * @param city2:第二个城市
         */
        convertpoint("广州市乐天创意园","深圳市保利剧院");
    }

    /**
     * 分别先计算出某地点的高德坐标
     * @param city1
     * @param city2
     */
    private void convertpoint(String city1,String city2) {
        GeocodeSearch(city1);
        GeocodeSearch(city2);
    }

    /**
     * 根据地点计算出高德坐标
     * @param city
     */
    //发起正地理编码搜索
    public void GeocodeSearch(String city) {
        //构造 GeocodeSearch 对象，并设置监听。
        geocodeSearch = new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(this);
//通过GeocodeQuery设置查询参数,调用getFromLocationNameAsyn(GeocodeQuery geocodeQuery) 方法发起请求。
//address表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode都ok
        GeocodeQuery query = new GeocodeQuery(city, city);
        geocodeSearch.getFromLocationNameAsyn(query);
    }

    /**
     * 根据两地坐标计算出的距离   ！！要注意是纬度在前
     * @param x1    A地的纬度
     * @param y1    A地的经度
     * @param x2    B地的纬度
     * @param y2    B地的经度
     */

    private void searchdistance(double x1,double y1,double x2,double y2) {
        DistanceSearch distanceSearch = new DistanceSearch(this);
        distanceSearch.setDistanceSearchListener(this);

        LatLonPoint start = new LatLonPoint(x1, y1);
//        LatLonPoint start1 = new LatLonPoint(39.90000, 116.407525);
//        LatLonPoint start2 = new LatLonPoint(38.540103, 76.978787);
//        LatLonPoint start3 = new LatLonPoint(10.90000, 116.407525);
        LatLonPoint dest = new LatLonPoint(x2, y2);

        DistanceSearch.DistanceQuery distanceQuery= new DistanceSearch.DistanceQuery();
//设置起点和终点，其中起点支持多个
        List<LatLonPoint> latLonPoints = new ArrayList<LatLonPoint>();
        latLonPoints.add(start);
//        latLonPoints.add(start1);
//        latLonPoints.add(start2);
//        latLonPoints.add(start3);
        distanceQuery.setOrigins(latLonPoints);
        distanceQuery.setDestination(dest);
        //设置测量方式，支持直线和驾车
        distanceQuery.setType(DistanceSearch.TYPE_DRIVING_DISTANCE);
        distanceSearch.calculateRouteDistanceAsyn(distanceQuery);
    }

    /**
     * 计算两地驾车距离的回调
     * @param distanceResult  回调详情  （见官方文档）
     * @param i 状态码
     */
    @Override
    public void onDistanceSearched(DistanceResult distanceResult, int i) {
        Toast.makeText(MainActivity.this,distanceResult.getDistanceResults().toString(),Toast.LENGTH_SHORT).show();
//        Log.i("距离",distanceResult.getDistanceResults().toString());
        Log.i("状态码",String.valueOf(i));
        List<DistanceItem> list=distanceResult.getDistanceResults();
        DistanceItem distanceItem=new DistanceItem();
        Log.i("长度",String.valueOf(list.get(0).getDistance()));
    }

    /**
     *
     * @param regeocodeResult
     * @param i
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

    }

    /**
     * 根据地点在List中加入经纬度
     * @param geocodeResult
     * @param i
     */
    //正地理编码
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        if (i == AMapException.CODE_AMAP_SUCCESS) {
            if (geocodeResult != null && geocodeResult.getGeocodeAddressList() != null
                    && geocodeResult.getGeocodeAddressList().size() > 0) {
                GeocodeAddress address = geocodeResult.getGeocodeAddressList().get(0);
                Log.i("个数",String.valueOf(geocodeResult.getGeocodeAddressList().size()));
                String addressName = "纬经度值:" + address.getLatLonPoint() + "\n位置描述:"
                        + address.getFormatAddress();
                Log.i("描述",addressName);
                //获取到的经纬度
                LatLonPoint latLongPoint = address.getLatLonPoint();
                double Lat = latLongPoint.getLatitude();
                double Lon = latLongPoint.getLongitude();
                Latitem.add(Lat);
                Lonitem.add(Lon);
                if (Latitem.size()==2){
                    Log.i("地点书","等于2了");
                    searchdistance(Latitem.get(0),Lonitem.get(0),Latitem.get(1),Lonitem.get(1));
                }
                Log.i("纬度",String.valueOf(Lat));
                Log.i("经度",String.valueOf(Lon));
            }
        }
    }
}
