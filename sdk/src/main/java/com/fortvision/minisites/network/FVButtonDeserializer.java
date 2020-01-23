package com.fortvision.minisites.network;

import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.fortvision.minisites.model.Anchor;
import com.fortvision.minisites.model.FVButton;
import com.fortvision.minisites.model.IframeButton;
import com.fortvision.minisites.model.ImageButton;
import com.fortvision.minisites.model.Popup;
import com.fortvision.minisites.model.VideoButton;
import com.fortvision.minisites.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.NumberFormat;

import static com.fortvision.minisites.utils.Utils.getJsonElementAsInt;
import static com.fortvision.minisites.utils.Utils.getJsonElementAsString;

/**
 * A class responsible to deserialize the json representation of the FV button data to our POJOs.
 */

public class FVButtonDeserializer implements JsonDeserializer<FVButton> {
    @Override
    public FVButton deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Log.d("JSON", json.toString());
        try {
            JsonObject object = json.getAsJsonObject();
            JsonArray campaignsData = object.get("campaignsData").getAsJsonArray();
            JsonObject element0 = campaignsData.get(0).getAsJsonObject();

            int width = Integer.parseInt(element0.get("button_width").getAsString().replace("px", ""));
            int height = Integer.parseInt(element0.get("button_height").getAsString().replace("px", ""));

            boolean dismissible = false;//object.get("allow_bubble_dismiss").getAsInt() == 1;
            int dismissSize = element0.get("dismiss_size").getAsInt();

            NumberFormat percentFormat = NumberFormat.getPercentInstance();
            Anchor anchor = new Anchor(percentFormat.parse(element0.get("button_initial_horizontal_position").getAsString()).floatValue(),
                    percentFormat.parse(element0.get("button_initial_vertical_position").getAsString()).floatValue(),
                    element0.get("button_initial_side").getAsString().equalsIgnoreCase("right")
            );

            String campaignId = element0.get("campaign_id").getAsString();
            int designId = element0.get("design_id").getAsInt();
            float opacity = element0.get("opacity_level").getAsFloat();
            int opacityTimeout = element0.get("opacity_timeout").getAsInt();

            String popupContent = element0.get("popup_content").getAsString()+"?useFbWeb=0";
            boolean preloadPopup = element0.get("preload_popup").getAsInt() == 1;

            int heightPixels = Utils.displayMetrics.heightPixels;
            int widthPixels = Utils.displayMetrics.widthPixels;

            int popupStartHPix = (int) Math.round(heightPixels / 100.0 * ((100 - Integer.parseInt(element0.get("popup_height").getAsString().replace("%", "")))/2));
            int popupStartWPix = (int) Math.round(widthPixels / 100.0 * ((100 - Integer.parseInt(element0.get("popup_width").getAsString().replace("%", "")))/4));

            int popupStartMargin = popupStartWPix;//getJsonElementAsInt(object.get("popup_horizontal_margin"), 0);
            int popupEndMargin = popupStartWPix;//getJsonElementAsInt(object.get("popup_horizontal_margin"), 0);
            int popupTopMargin = popupStartHPix;//getJsonElementAsInt(object.get("popup_top_margin"), 0);
            int popupBottomMargin = popupStartHPix;//getJsonElementAsInt(object.get("popup_bottom_margin"), 0);

            Popup popup = new Popup(popupContent, preloadPopup, popupStartMargin, popupEndMargin, popupTopMargin, popupBottomMargin);
            if (element0.get("is_video_campaign").getAsInt() == 1) {
                int bigWidth = object.get("video_width").getAsInt();
                int bigHeight = object.get("video_height").getAsInt();
                return new VideoButton(dismissible, dismissSize, width, height, anchor, campaignId, designId, opacity, opacityTimeout, popup,
                        object.get("video_url").getAsString(), bigWidth, bigHeight);
            } else {
                if (element0.get("is_button_iframe").getAsInt() == 1) {
                    return new IframeButton(dismissible, dismissSize, width, height, anchor, campaignId, designId, opacity, opacityTimeout, popup,
                            element0.get("button_iframe_url").getAsString()+"?useFbWeb=0");
                } else {
                    return new ImageButton(dismissible, dismissSize, width, height, anchor, campaignId, designId, opacity, opacityTimeout, popup,
                            getJsonElementAsString(object.get("button_imgL"), null), getJsonElementAsString(object.get("button_imgR"), null), object.get("button_imgC").getAsString());
                }
            }
        } catch (Exception e) {
            throw new JsonParseException("Could not parse the json object", e);
        }
    }
}
