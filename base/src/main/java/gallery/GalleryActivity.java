package gallery;

/**
 * Created by John on 2016/8/1.
 */
//public class GalleryActivity extends Activity {
//    public static final String URL_KEY = "GalleryActivity_url";
//    public static final String URL_INDEX = "GalleryActivity_index";
//    private TextView textView;
//    private String[] imageUrls;
//    private int index;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
//        super.onCreate(savedInstanceState, persistentState);
//        setContentView(R.layout.activity_gallery);
//        imageUrls=getIntent().getStringArrayExtra(URL_KEY);
//        index=getIntent().getIntExtra(URL_INDEX,0);
//        initWidget();
//    }
//
//    private void initWidget() {
//        textView = (TextView) findViewById(R.id.page_title);
//        if (imageUrls.length < 2) {
//            textView.setVisibility(View.GONE);
//        } else {
//            textView.setText(String.format("%d/%d", index + 1, imageUrls.length));
//        }
//
//        HackyViewPager mViewPager = (HackyViewPager) findViewById(R.id.view_pager);
//        mViewPager.setAdapter(new SamplePagerAdapter(this, imageUrls));
//        mViewPager.setCurrentItem(index);
//        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int
//                    positionOffsetPixels) {
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                textView.setText(String.format("%d/%d", position + 1, imageUrls.length));
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//            }
//        });
//    }
//static class SamplePagerAdapter extends PagerAdapter {
//
//    private static final int[] sDrawables = { R.drawable.wallpaper, R.drawable.wallpaper, R.drawable.wallpaper,
//            R.drawable.wallpaper, R.drawable.wallpaper, R.drawable.wallpaper };
//
//    @Override
//    public int getCount() {
//        return sDrawables.length;
//    }
//
//    @Override
//    public View instantiateItem(ViewGroup container, int position) {
//        PhotoView photoView = new PhotoView(container.getContext());
//        photoView.setImageResource(sDrawables[position]);
//
//        // Now just add PhotoView to ViewPager and return it
//        container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//
//        return photoView;
//    }
//
//    @Override
//    public void destroyItem(ViewGroup container, int position, Object object) {
//        container.removeView((View) object);
//    }
//
//    @Override
//    public boolean isViewFromObject(View view, Object object) {
//        return view == object;
//    }
//
//}
//}
