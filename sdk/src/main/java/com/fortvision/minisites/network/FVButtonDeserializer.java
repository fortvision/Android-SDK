package com.fortvision.minisites.network;

import android.util.Log;

import com.fortvision.minisites.model.Anchor;
import com.fortvision.minisites.model.DimensionedSize;
import com.fortvision.minisites.model.FVButton;
import com.fortvision.minisites.model.IframeButton;
import com.fortvision.minisites.model.ImageButton;
import com.fortvision.minisites.model.AutoClickButton;
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

import static com.fortvision.minisites.utils.Utils.getJsonElementAsString;

/**
 * A class responsible to deserialize the json representation of the FV button data to our POJOs.
 */

public class FVButtonDeserializer implements JsonDeserializer<FVButton> {

    public DimensionedSize getDimensionSize(double size, String dimension, double density) {
        return new DimensionedSize(size / density, dimension);
    }

    public DimensionedSize getDimensionSize(String size, String dimension) {
        return getDimensionSize(Integer.parseInt(size.replace(dimension, "")), dimension, 1);
    }

    public DimensionedSize getDimensionSize(String size, String dimension, double density) {
        return getDimensionSize(Double.parseDouble(size.replace(dimension, "")), dimension, density);
    }

    @Override
    public FVButton deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Log.d("JSON", json.toString());
        try {
            JsonObject object = json.getAsJsonObject();
            JsonArray campaignsData = object.get("campaignsData").getAsJsonArray();
            JsonObject element0 = campaignsData.get(0).getAsJsonObject();

            DimensionedSize width = null;
            if (element0.get("button_width").getAsString().endsWith("px")) {
                width = getDimensionSize(element0.get("button_width").getAsString(), "px");
            } else if (element0.get("button_width").getAsString().endsWith("%")) {
                width = getDimensionSize(element0.get("button_width").getAsString(), "%");
            }

            DimensionedSize height = null;
            if (element0.get("button_height").getAsString().endsWith("px")) {
                height = getDimensionSize(element0.get("button_height").getAsString(), "px");
            } else if (element0.get("button_height").getAsString().endsWith("%")) {
                height = getDimensionSize(element0.get("button_height").getAsString(), "%");
            }

            boolean dismissible = false;//object.get("allow_bubble_dismiss").getAsInt() == 1;
            int dismissSize = element0.get("dismiss_size").getAsInt();

            DimensionedSize button_initial_horizontal_position = null;
            if (element0.get("button_initial_horizontal_position").getAsString().endsWith("px")) {
                button_initial_horizontal_position = getDimensionSize(element0.get("button_initial_horizontal_position").getAsString(), "px");
            } else if (element0.get("popup_width").getAsString().endsWith("%")) {
                button_initial_horizontal_position = getDimensionSize(element0.get("button_initial_horizontal_position").getAsString(), "%");
            }

            DimensionedSize button_initial_vertical_position = null;
            if (element0.get("button_initial_vertical_position").getAsString().endsWith("px")) {
                button_initial_vertical_position = getDimensionSize(element0.get("button_initial_vertical_position").getAsString(), "px");
            } else if (element0.get("popup_width").getAsString().endsWith("%")) {
                button_initial_vertical_position = getDimensionSize(element0.get("button_initial_vertical_position").getAsString(), "%");
            }

            Anchor anchor = new Anchor(button_initial_horizontal_position,
                    button_initial_vertical_position,
                    element0.get("button_initial_side").getAsString().equalsIgnoreCase("right")
            );

            String campaignId = element0.get("campaign_id").getAsString();
            int designId = element0.get("design_id").getAsInt();
            float opacity = element0.get("opacity_level").getAsFloat();
            int opacityTimeout = element0.get("opacity_timeout").getAsInt();

            String popupContent = element0.get("popup_content").getAsString()/*"https://fortcdn.com/Campaigns-react/151304/66431470-3d58-11ea-8687-a325461d57b1"*/ + "?useFbWeb=0";
            boolean preloadPopup = element0.get("preload_popup").getAsInt() == 1;

            double heightPixels = Utils.displayMetrics.heightPixels / Utils.density;
            double widthPixels = Utils.displayMetrics.widthPixels / Utils.density;

            DimensionedSize popupStartHPix = null;
            if (element0.get("popup_height").getAsString().endsWith("px")) {
                popupStartHPix = getDimensionSize(element0.get("popup_height").getAsString(), "px");
            } else if (element0.get("popup_height").getAsString().endsWith("%")) {
                popupStartHPix = getDimensionSize(element0.get("popup_height").getAsString(), "%");
            }
            popupStartHPix.setSize(heightPixels * (100 - popupStartHPix.getSize()) / 200.0);

            DimensionedSize popupStartWPix = null;
            if (element0.get("popup_width").getAsString().endsWith("px")) {
                popupStartWPix = getDimensionSize(element0.get("popup_width").getAsString(), "px");
            } else if (element0.get("popup_width").getAsString().endsWith("%")) {
                popupStartWPix = getDimensionSize(element0.get("popup_width").getAsString(), "%");
            }
            popupStartWPix.setSize(widthPixels * (100 - popupStartWPix.getSize()) / 200.0);

            int popupStartMargin = popupStartWPix.toInt();//getJsonElementAsInt(object.get("popup_horizontal_margin"), 0);
            int popupEndMargin = popupStartWPix.toInt();//getJsonElementAsInt(object.get("popup_horizontal_margin"), 0);
            int popupTopMargin = popupStartHPix.toInt();//getJsonElementAsInt(object.get("popup_top_margin"), 0);
            int popupBottomMargin = popupStartHPix.toInt();//getJsonElementAsInt(object.get("popup_bottom_margin"), 0);

            DimensionedSize popup_width;
            DimensionedSize popup_height;
            if (element0.get("popup_width").getAsString().endsWith("%")) {
                popup_width = getDimensionSize(element0.get("popup_width").getAsString(), "%");
            } else {
                popup_width = getDimensionSize(element0.get("popup_width").getAsString(), "px");
            }
            if (element0.get("popup_height").getAsString().endsWith("%")) {
                popup_height = getDimensionSize(element0.get("popup_height").getAsString(), "%");
            } else {
                popup_height = getDimensionSize(element0.get("popup_height").getAsString(), "px");
            }

            Popup popup = new Popup(popupContent, preloadPopup, popupStartMargin, popupEndMargin, popupTopMargin, popupBottomMargin, popup_width.isPercent(), popup_width.getSize(), popup_height.getSize());
            if (element0.get("is_video_campaign").getAsInt() == 1) {
                int bigWidth = object.get("video_width").getAsInt();
                int bigHeight = object.get("video_height").getAsInt();
                return new VideoButton(dismissible, dismissSize, width, height, anchor, campaignId, designId, opacity, opacityTimeout, popup,
                        object.get("video_url").getAsString(), bigWidth, bigHeight);
            } else if (element0.get("is_button_iframe").getAsInt() == 1) {
                return new IframeButton(dismissible, dismissSize, width, height, anchor, campaignId, designId, opacity, opacityTimeout, popup,
                        element0.get("button_iframe_url").getAsString() + "?useFbWeb=0");
            } else if (object.get("button_imgL") != null) {
                return new ImageButton(dismissible, dismissSize, width, height, anchor, campaignId, designId, opacity, opacityTimeout, popup,
                        getJsonElementAsString(object.get("button_imgL"), null), getJsonElementAsString(object.get("button_imgR"), null), object.get("button_imgC").getAsString());
            } else {
                return new AutoClickButton(dismissible, dismissSize, width, height, anchor, campaignId, designId, opacity, opacityTimeout, popup);
            }
        } catch (Exception e) {
            throw new JsonParseException("Could not parse the json object", e);
        }
    }
}
