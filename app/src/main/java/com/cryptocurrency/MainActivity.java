package com.cryptocurrency;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.cryptocurrency.databinding.ActivityMainBinding;
import com.cryptocurrency.models.cryptolistmodel.AllMarketModel;
import com.cryptocurrency.models.cryptolistmodel.CryptoMarketDataModel;
import com.cryptocurrency.viewmodel.AppViewModel;
import com.google.android.material.snackbar.Snackbar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @Inject
    ConnectivityManager connectivityManager;

    @Inject
    NetworkRequest networkRequest;

    ActivityMainBinding binding;
    NavHostFragment navHostFragment;
    NavController navController;
    AppBarConfiguration appBarConfiguration;
    public DrawerLayout drawerLayout;
    AppViewModel appViewModel;
    CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        drawerLayout = binding.drawerLayout;

        compositeDisposable = new CompositeDisposable();

        setAppViewModel();
        setupNavigationComponent();
        checkConnection();
    }

    private void checkConnection() {
        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@androidx.annotation.NonNull Network network) {
                Log.e("TAG", "onAvailable: ");
                callListApiRequest();
                callCryptoMarketApiRequest();
            }

            @Override
            public void onLost(@androidx.annotation.NonNull Network network) {
                Log.e("TAG", "onLost: ");
                Snackbar.make(binding.mainCon, "No Internet, Please Connect again", 4000).show();
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        } else {
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
        }
    }

    private void callCryptoMarketApiRequest() {
        Completable.fromRunnable(() -> {

                    try {
                        Document pageSrc = Jsoup.connect("https://coinmarketcap.com/").get();

                        Elements scrapeMarketData = pageSrc.getElementsByClass("cmc-link");
                        String[] dominance_txt = scrapeMarketData.get(4).text().split(" ");

                        // Scraping Market number of changes like (Market-capChange,volumeChange,...)
                        Elements ScrapeMarketChange = pageSrc.getElementsByClass("sc-27sy12-0 gLZJFn");
                        String[] changePercent = ScrapeMarketChange.text().split(" ");

                        // Scraping All span Tag
                        Elements ScrapeChangeIcon = pageSrc.getElementsByTag("span");

                        // get all span Tag wth Icon (class= caretUp and caretDown)
                        ArrayList<String> IconList = new ArrayList<>();
                        for (Element i : ScrapeChangeIcon) {
                            if (i.hasClass("icon-Caret-down") || i.hasClass("icon-Caret-up")) {
                                IconList.add(i.attr("class"));
                            }
                        }

                        // matching - or + element of PercentChanges
                        ArrayList<String> finalChangePercent = new ArrayList<>();
                        for (int i = 0; i < 3; i++) {
                            if (IconList.get(i).equals("icon-Caret-up")) {
                                finalChangePercent.add(changePercent[i]);
                            } else {
                                finalChangePercent.add("-" + changePercent[i]);
                            }
                        }


                        String cryptos = scrapeMarketData.get(0).text();
                        String exchanges = scrapeMarketData.get(1).text();
                        String market_cap = scrapeMarketData.get(2).text();
                        String vol_24 = scrapeMarketData.get(3).text();

                        String BTC_dominance = dominance_txt[1];
                        String ETH_dominance = dominance_txt[3];

                        String MarketCap_change = finalChangePercent.get(0);
                        String vol_change = finalChangePercent.get(1);
                        String BTCD_change = finalChangePercent.get(2);


                        CryptoMarketDataModel cryptoMarketDataModel = new CryptoMarketDataModel(cryptos, exchanges, market_cap, vol_24, BTC_dominance, ETH_dominance, MarketCap_change, vol_change, BTCD_change);

                        appViewModel.insertCryptoDataMarket(cryptoMarketDataModel);
                        // Log.e("TAG", "run: " + scrapeMarketData.get(0).text());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        Log.e("TAG", "onComplete: Scrape is done");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });

    }


    private void setAppViewModel() {
        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);
    }


    private void callListApiRequest() {
        Observable.interval(20, TimeUnit.SECONDS)
                .flatMap(n -> appViewModel.marketFutureCall().get())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AllMarketModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull AllMarketModel allMarketModel) {

                        appViewModel.insertAllMarket(allMarketModel);

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("TAG", "onError: ", e);
                    }

                    @Override
                    public void onComplete() {
                        Log.e("TAG", "onComplete: ");
                    }
                });
    }

    private void setupNavigationComponent() {
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        //setup drawer layout
        appBarConfiguration = new AppBarConfiguration.Builder(R.id.homeFragment, R.id.marketFragment, R.id.watchListFragment).setOpenableLayout(binding.drawerLayout).build();

        NavigationUI.setupWithNavController(binding.navigationView, navController);

        binding.navigationView.setNavigationItemSelectedListener(item -> {

            if (item.getItemId() == R.id.exit) {
                finish();
            } else {
                NavigationUI.onNavDestinationSelected(item, navController);
                binding.drawerLayout.closeDrawers();
            }
            return false;
        });

        setupSmoothBottomNavigation();
    }

    private void setupSmoothBottomNavigation() {
        PopupMenu popupMenu = new PopupMenu(this, null);
        popupMenu.inflate(R.menu.menu);
        Menu menu = popupMenu.getMenu();
        binding.bottomBar.setupWithNavController(menu, navController);
//        binding.bottomBar.setupWithNavController(menu, navController)
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}