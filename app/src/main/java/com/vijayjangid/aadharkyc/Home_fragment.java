package com.vijayjangid.aadharkyc;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Base64;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.vijayjangid.aadharkyc.databinding.FragmentHomeFragmentBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import Aeps.AepsActivity;
import Electricity.ElecRecharge;
import FastTag.Fastag;
import Gas.GasRecharge;
import Insurance.Insurance;
import Loan.LoanRecharge;
import MobRecharge.RechargePrepaid;
import Water.Water_Bill_activity;
import a2z_wallet.A2ZWalletActivity;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static maes.tech.intentanim.CustomIntent.customType;

public class Home_fragment extends Fragment
        implements View.OnClickListener {

    /* NOTE - VIEW BINDING ENABLED FOR THIS ACTIVITY */

    ImageView iv_scanPay, iv_sendMoney, iv_sendAgain,
            iv_prepaid, iv_electricity, iv_water, iv_insurance,
            iv_loanRepay, iv_gas, iv_dth, iv_dataCard, iv_fasTag;

    FragmentHomeFragmentBinding fragmentHomeFragmentBinding;
    TextView tv_scanPay, tv_sendMoney, tv_sendAgain,
            tv_prepaid, tv_electricity, tv_water, tv_insurance,
            tv_loan, tv_gas, tv_dth, tv_dataCard, tv_fasTag, tv_shareCode,
            tvb_shareCode;

    static View root2;
    private int activity_view = R.layout.fragment_home_fragment;
    RecyclerView recyclerView;

    Animation animation;
    ScrollView scrollView;
    View root;

    /*copied from other*/
    public static final String PROVIDER_TYPE = "provider_type";
    public static final String PROVIDER = "provider";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentHomeFragmentBinding = FragmentHomeFragmentBinding.inflate(getLayoutInflater());
        root = fragmentHomeFragmentBinding.getRoot();
        root2 = fragmentHomeFragmentBinding.getRoot();

        idAndListeners();
        newFeature();

        //ShowIntro("Scan to pay", "Scan any QR code to pay", R.id.scanpay_iv, 1);


        return root;
    }

    private void ShowIntro(String title, String text, int viewId, final int type) {

        new GuideView.Builder(getContext())
                .setTitle(title)
                .setContentText(text)
                .setTargetView(root.findViewById(viewId))
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setDismissType(GuideView.DismissType.anywhere) //optional - default dismissible by TargetView
                .setGuideListener(new GuideView.GuideListener() {
                    @Override
                    public void onDismiss(View view) {

                        if (type == 1) {
                            ShowIntro("Pay", "Send money to friends using UPI", R.id.sendmoney_iv, 2);
                        } else if (type == 2) {
                            ShowIntro("Wallet", "Manage your wallet", R.id.sendagain_iv, 3);
                        } else if (type == 3) {
                            ShowIntro("Pay from home", "ElecRecharge and pay your bills from home", R.id.rechargelll, 4);
                        } else if (type == 4) {
                            scrollView.post(new Runnable() {
                                @Override
                                public void run() {
                                    scrollView.scrollTo(0, root.findViewById(R.id.invitelll).getBottom());
                                }
                            });

                            ShowIntro("Share", "Invite your friends and earn when they send their first payment", R.id.invitelll, 6);
                        } else if (type == 6) {
                            Toast.makeText(getContext(), "Welcome to IOLab", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .build()
                .show();
    }

    // setting all id and OnClickListeners
    void idAndListeners() {

        iv_scanPay = fragmentHomeFragmentBinding.scanpayIv;
        iv_sendMoney = fragmentHomeFragmentBinding.sendmoneyIv;
        iv_sendAgain = fragmentHomeFragmentBinding.sendagainIv;
        iv_prepaid = fragmentHomeFragmentBinding.prepaidIv;
        iv_electricity = fragmentHomeFragmentBinding.electricityIv;
        iv_water = fragmentHomeFragmentBinding.waterIv;
        iv_insurance = fragmentHomeFragmentBinding.insuranceIv;
        iv_loanRepay = fragmentHomeFragmentBinding.ivLoanRepay;
        iv_gas = fragmentHomeFragmentBinding.ivGas;
        iv_dth = fragmentHomeFragmentBinding.dthIv;
        iv_dataCard = fragmentHomeFragmentBinding.datacardIv;
        iv_fasTag = fragmentHomeFragmentBinding.fastagIv;

        tv_scanPay = fragmentHomeFragmentBinding.scanpayTv;
        tv_sendMoney = fragmentHomeFragmentBinding.sendmoneyTv;
        tv_sendAgain = fragmentHomeFragmentBinding.sendAgainTv;
        tv_prepaid = fragmentHomeFragmentBinding.prepaidTv;
        tv_electricity = fragmentHomeFragmentBinding.electricityTv;
        tv_water = fragmentHomeFragmentBinding.waterTv;
        tv_insurance = fragmentHomeFragmentBinding.insuranceTv;
        tv_loan = fragmentHomeFragmentBinding.tvLoanRepay;
        tv_gas = fragmentHomeFragmentBinding.tvGas;
        tv_dth = fragmentHomeFragmentBinding.dthTv;
        tv_dataCard = fragmentHomeFragmentBinding.datacardTv;
        tv_fasTag = fragmentHomeFragmentBinding.fastagTv;
        recyclerView = fragmentHomeFragmentBinding.viewPagerAddMoney;
        tv_shareCode = fragmentHomeFragmentBinding.shareCodeFragment;
        tvb_shareCode = fragmentHomeFragmentBinding.btnInviteFragment;
        scrollView = fragmentHomeFragmentBinding.homescrollview;

        // making animation
        animation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_aplha);

        iv_prepaid.setOnClickListener(this);
        iv_scanPay.setOnClickListener(this);
        iv_sendMoney.setOnClickListener(this);
        iv_sendAgain.setOnClickListener(this);
        iv_electricity.setOnClickListener(this);
        iv_water.setOnClickListener(this);
        iv_loanRepay.setOnClickListener(this);
        iv_insurance.setOnClickListener(this);
        iv_gas.setOnClickListener(this);
        iv_dth.setOnClickListener(this);
        iv_fasTag.setOnClickListener(this);
        iv_dataCard.setOnClickListener(this);

        tv_scanPay.setOnClickListener(this);
        tv_sendMoney.setOnClickListener(this);
        tv_sendAgain.setOnClickListener(this);
        tv_prepaid.setOnClickListener(this);
        tv_electricity.setOnClickListener(this);
        tv_water.setOnClickListener(this);
        tv_insurance.setOnClickListener(this);
        tv_loan.setOnClickListener(this);
        tv_gas.setOnClickListener(this);
        tv_dth.setOnClickListener(this);
        tv_dataCard.setOnClickListener(this);
        tv_fasTag.setOnClickListener(this);
        tvb_shareCode.setOnClickListener(this);
        tv_shareCode.setOnClickListener(this);
    }

    /* recycler view android */
    void newFeature() {

        ArrayList<RecyclerEntity> arrayList = new ArrayList<>();
        RecyclerEntity entity = new RecyclerEntity();
        arrayList.add(entity);

        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(getContext(), arrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

/*
        ItemTouchHelper.SimpleCallback touchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            private final ColorDrawable background = new ColorDrawable(Color.LTGRAY);

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.showMenu(viewHolder.getAdapterPosition());
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                View itemView = viewHolder.itemView;

                if (dX > 0) {
                    background.setBounds(itemView.getLeft(), itemView.getTop(),
                            itemView.getLeft() + ((int) dX), itemView.getBottom());

                } else if (dX < 0) {
                    background.setBounds(itemView.getRight() + ((int) dX), itemView.getTop(),
                            itemView.getRight(), itemView.getBottom());

                } else {
                    background.setBounds(0, 0, 0, 0);
                }

                background.draw(c);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelperCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);*/

        SwipeHelper swipeHelper = new SwipeHelper(getContext(), recyclerView) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder
                    , List<UnderlayButton> underlayButtons) {

                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "Manage Wallet",
                        0,
                        Color.TRANSPARENT,
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                startActivity(new Intent(getActivity(), WalletManage.class));
                            }
                        }
                ));

            }
        };
    }

    boolean checkCameraPermission() {
        /* calling this method checks if location permission is given or not
        if not then ask and if yes then skips */

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA}, 1);
        }

        return ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onClick(View v) {
        float alphaVal = (float) 0.5;

        int id = v.getId();
        switch (id) {
            case R.id.scanpay_tv:
            case R.id.scanpay_iv:
                iv_scanPay.setAlpha(alphaVal);
                tv_scanPay.setAlpha(alphaVal);
                if (!checkCameraPermission()) {
                    startActivity(new Intent(getActivity(), AepsActivity.class));
                }
                break;

            case R.id.sendmoney_tv:
            case R.id.sendmoney_iv:
                iv_sendMoney.setAlpha(alphaVal);
                tv_sendMoney.setAlpha(alphaVal);
                startActivity(new Intent(getContext(), A2ZWalletActivity.class));
                break;

            case R.id.sendAgain_tv:
            case R.id.sendagain_iv:
                iv_sendAgain.setAlpha(alphaVal);
                tv_sendAgain.setAlpha(alphaVal);
                break;

            case R.id.prepaid_tv:
            case R.id.prepaid_iv:
                iv_prepaid.setAlpha(alphaVal);
                tv_prepaid.setAlpha(alphaVal);
                startActivity(new Intent(getContext(), RechargePrepaid.class));
                customType(getContext(), "fadein-to-fadeout");
                break;

            case R.id.electricity_tv:
            case R.id.electricity_iv:
                iv_electricity.setAlpha(alphaVal);
                tv_electricity.setAlpha(alphaVal);
                startActivity(new Intent(getContext(), ElecRecharge.class));
                customType(getContext(), "fadein-to-fadeout");
                break;

            case R.id.water_tv:
            case R.id.water_iv:
                iv_water.setAlpha(alphaVal);
                tv_water.setAlpha(alphaVal);
                startActivity(new Intent(getContext(), Water_Bill_activity.class));
                customType(getContext(), "fadein-to-fadeout");
                break;

            case R.id.insurance_tv:
            case R.id.insurance_iv:
                iv_insurance.setAlpha(alphaVal);
                tv_insurance.setAlpha(alphaVal);
                startActivity(new Intent(getContext(), Insurance.class));
                customType(getContext(), "fadein-to-fadeout");
                break;

            case R.id.iv_loanRepay:
            case R.id.tv_loanRepay:
                iv_loanRepay.setAlpha(alphaVal);
                tv_loan.setAlpha(alphaVal);
                startActivity(new Intent(getContext(), LoanRecharge.class));
                customType(getContext(), "fadein-to-fadeout");
                break;

            case R.id.tv_gas:
            case R.id.iv_gas:
                iv_gas.setAlpha(alphaVal);
                tv_gas.setAlpha(alphaVal);
                startActivity(new Intent(getContext(), GasRecharge.class));
                customType(getContext(), "fadein-to-fadeout");
                break;

            case R.id.dth_tv:
            case R.id.dth_iv:
                iv_dth.setAlpha(alphaVal);
                tv_dth.setAlpha(alphaVal);
                startActivity(new Intent(getContext(), Dth.DthRecharge.class));
                customType(getContext(), "fadein-to-fadeout");
                break;

            case R.id.datacard_tv:
            case R.id.datacard_iv:
                iv_dataCard.setAlpha(alphaVal);
                tv_dataCard.setAlpha(alphaVal);

                break;

            case R.id.fastag_iv:
            case R.id.fastag_tv:
                iv_fasTag.setAlpha(alphaVal);
                tv_fasTag.setAlpha(alphaVal);
                startActivity(new Intent(getContext(), Fastag.class));
                break;

            case R.id.shareCodeFragment:
                Toast.makeText(getContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_inviteFragment:
                startActivity(new Intent(getContext(), InviteAndEarn.class));
                break;
        }
    }

    public abstract static class SwipeHelper extends ItemTouchHelper.SimpleCallback {

        public static final int BUTTON_WIDTH = 300;
        private RecyclerView recyclerView;
        private List<UnderlayButton> buttons;
        private GestureDetector gestureDetector;
        private int swipedPos = -1;
        private float swipeThreshold = 0.5f;
        private Map<Integer, List<UnderlayButton>> buttonsBuffer;
        private Queue<Integer> recoverQueue;

        private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                for (UnderlayButton button : buttons) {
                    if (button.onClick(e.getX(), e.getY()))
                        break;
                }
                return true;
            }
        };

        private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent e) {
                if (swipedPos < 0) return false;
                Point point = new Point((int) e.getRawX(), (int) e.getRawY());

                RecyclerView.ViewHolder swipedViewHolder = recyclerView.findViewHolderForAdapterPosition(swipedPos);
                View swipedItem = swipedViewHolder.itemView;
                Rect rect = new Rect();
                swipedItem.getGlobalVisibleRect(rect);

                if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_UP
                        || e.getAction() == MotionEvent.ACTION_MOVE) {
                    if (rect.top < point.y && rect.bottom > point.y)
                        gestureDetector.onTouchEvent(e);
                    else {
                        recoverQueue.add(swipedPos);
                        swipedPos = -1;
                        recoverSwipedItem();
                    }
                }
                return false;
            }
        };

        public SwipeHelper(Context context, RecyclerView recyclerView) {
            super(0, ItemTouchHelper.LEFT);
            this.recyclerView = recyclerView;
            this.buttons = new ArrayList<>();
            this.gestureDetector = new GestureDetector(context, gestureListener);
            this.recyclerView.setOnTouchListener(onTouchListener);
            buttonsBuffer = new HashMap<>();
            recoverQueue = new LinkedList<Integer>() {
                @Override
                public boolean add(Integer o) {
                    if (contains(o))
                        return false;
                    else
                        return super.add(o);
                }
            };

            attachSwipe();
        }


        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int pos = viewHolder.getAdapterPosition();

            root2.getContext().startActivity(new Intent(root2.getContext(), WalletManage.class));

            if (swipedPos != pos)
                recoverQueue.add(swipedPos);

            swipedPos = pos;

            if (buttonsBuffer.containsKey(swipedPos))
                buttons = buttonsBuffer.get(swipedPos);
            else
                buttons.clear();

            buttonsBuffer.clear();
            swipeThreshold = 0.5f * buttons.size() * BUTTON_WIDTH;
            recoverSwipedItem();
        }

        @Override
        public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
            return swipeThreshold;
        }

        @Override
        public float getSwipeEscapeVelocity(float defaultValue) {
            return 0.1f * defaultValue;
        }

        @Override
        public float getSwipeVelocityThreshold(float defaultValue) {
            return 5.0f * defaultValue;
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            int pos = viewHolder.getAdapterPosition();
            float translationX = dX;
            View itemView = viewHolder.itemView;

            if (pos < 0) {
                swipedPos = pos;
                return;
            }

            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                if (dX < 0) {
                    List<UnderlayButton> buffer = new ArrayList<>();

                    if (!buttonsBuffer.containsKey(pos)) {
                        instantiateUnderlayButton(viewHolder, buffer);
                        buttonsBuffer.put(pos, buffer);
                    } else {
                        buffer = buttonsBuffer.get(pos);
                    }

                    translationX = dX * buffer.size() * BUTTON_WIDTH / itemView.getWidth();
                    drawButtons(c, itemView, buffer, pos, translationX);
                }
            }

            super.onChildDraw(c, recyclerView, viewHolder, translationX, dY, actionState, isCurrentlyActive);
        }

        private synchronized void recoverSwipedItem() {
            while (!recoverQueue.isEmpty()) {
                int pos = recoverQueue.poll();
                if (pos > -1) {
                    recyclerView.getAdapter().notifyItemChanged(pos);
                }
            }
        }

        private void drawButtons(Canvas c, View itemView, List<UnderlayButton> buffer, int pos, float dX) {
            float right = itemView.getRight();
            float dButtonWidth = (-1) * dX / buffer.size();

            for (UnderlayButton button : buffer) {
                float left = right - dButtonWidth;
                button.onDraw(
                        c,
                        new RectF(
                                left,
                                itemView.getTop(),
                                right,
                                itemView.getBottom()
                        ),
                        pos
                );

                right = left;
            }
        }

        public void attachSwipe() {
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(this);
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }

        public abstract void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons);

        public interface UnderlayButtonClickListener {
            void onClick(int pos);
        }

        public class UnderlayButton {
            private String text;
            private int imageResId;
            private int color;
            private int pos;
            private RectF clickRegion;
            private UnderlayButtonClickListener clickListener;

            public UnderlayButton(String text, int imageResId, int color, UnderlayButtonClickListener clickListener) {
                this.text = text;
                this.imageResId = imageResId;
                this.color = color;
                this.clickListener = clickListener;
            }

            public boolean onClick(float x, float y) {
                if (clickRegion != null && clickRegion.contains(x, y)) {
                    clickListener.onClick(pos);
                    return true;
                }

                return false;
            }

            public void onDraw(Canvas c, RectF rect, int pos) {
                Paint p = new Paint();

                // Draw background
                p.setColor(color);
                c.drawRect(rect, p);

                // Draw Text
                p.setColor(Color.BLACK);
                p.setTextSize(36);

                Rect r = new Rect();
                float cHeight = rect.height();
                float cWidth = rect.width();
                p.setTextAlign(Paint.Align.LEFT);
                p.getTextBounds(text, 0, text.length(), r);
                float x = cWidth / 2f - r.width() / 2f - r.left;
                float y = cHeight / 2f + r.height() / 2f - r.bottom;
                c.drawText(text, rect.left + x, rect.top + y, p);

                clickRegion = rect;
                this.pos = pos;
            }
        }
    }

    /*public class SwipeController extends ItemTouchHelper.Callback {


        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(0, LEFT | RIGHT);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target) {

            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        }
    }*/

    void showProgressBar(boolean showText, String text) {

        /*to customize the progress bar then go to
         * progressbar_viewxml.xml in layout folder*/

        View view = getLayoutInflater().inflate(R.layout.layout_progressbar, null);
        if (view.getParent() != null) ((ViewGroup) view.getParent()).removeView(view);

        CircularProgressIndicator lpi = view.findViewById(R.id.home_progress_bar);
        TextView textView = view.findViewById(R.id.progress_text_tv);
        if (showText) textView.setText(text);
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setView(view);
        alert.setCancelable(false);
        alert.setIcon(null);
        alert.show();
    }

    public class RecyclerEntity {
        private String title;
        private boolean showMenu = false;
        private int image;

        public RecyclerEntity() {
        }

        public RecyclerEntity(String title, int image, boolean showMenu) {
            this.title = title;
            this.showMenu = showMenu;
            this.image = image;
        }

        public int getImage() {
            return image;
        }

        public void setImage(int image) {
            this.image = image;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public boolean isShowMenu() {
            return showMenu;
        }

        public void setShowMenu(boolean showMenu) {
            this.showMenu = showMenu;
        }
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<RecyclerEntity> list;
        private LayoutInflater mInflater;

        RecyclerViewAdapter(Context context, List<RecyclerEntity> data) {
            this.mInflater = LayoutInflater.from(context);
            this.list = data;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = mInflater.inflate(R.layout.listview_wallet_info, parent, false);
            TextView mobileNumberUser = view.findViewById(R.id.tv_mobileNumVP);
            TextView walletBalance = view.findViewById(R.id.tv_walletBalanceVP);
            ImageView userPic = view.findViewById(R.id.iv_vp);

            UserData userData = new UserData(getContext());
            mobileNumberUser.setText(userData.getMobile());
            //walletBalance.setText("100");

            byte[] decodedString = Base64.decode(userData.getPhotoByteCode(), 0);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            if (userData.getPhotoByteCode().length() > 200) userPic.setImageBitmap(bitmap);

            return new ViewHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        /*this comes again and again*/

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void showMenu(int position) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setShowMenu(false);
            }
            list.get(position).setShowMenu(true);
            notifyDataSetChanged();
        }

        public boolean isMenuShown() {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isShowMenu()) {
                    return true;
                }
            }
            return false;
        }

        public void closeMenu() {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setShowMenu(false);
            }
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ViewHolder(View itemView) {
                super(itemView);
                /*here will be id and on click listeners*/
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        float normalDark = 1;

        iv_scanPay.setAlpha(normalDark);
        tv_scanPay.setAlpha(normalDark);
        iv_sendMoney.setAlpha(normalDark);
        tv_sendMoney.setAlpha(normalDark);
        iv_sendAgain.setAlpha(normalDark);
        tv_sendAgain.setAlpha(normalDark);
        iv_prepaid.setAlpha(normalDark);
        tv_prepaid.setAlpha(normalDark);
        iv_electricity.setAlpha(normalDark);
        tv_electricity.setAlpha(normalDark);
        iv_water.setAlpha(normalDark);
        tv_water.setAlpha(normalDark);
        iv_insurance.setAlpha(normalDark);
        tv_insurance.setAlpha(normalDark);
        iv_loanRepay.setAlpha(normalDark);
        tv_loan.setAlpha(normalDark);
        iv_gas.setAlpha(normalDark);
        tv_gas.setAlpha(normalDark);
        iv_dth.setAlpha(normalDark);
        tv_dth.setAlpha(normalDark);
        iv_dataCard.setAlpha(normalDark);
        tv_dataCard.setAlpha(normalDark);
        iv_fasTag.setAlpha(normalDark);
        tv_fasTag.setAlpha(normalDark);
    }

    // for scanner
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                Toast.makeText(getContext(), contents, Toast.LENGTH_SHORT).show();
            }
            if (resultCode == RESULT_CANCELED) {
                //handle cancel
                Toast.makeText(getContext(), "Scanner cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

}