package com.fortvision.minisites.network;

import android.util.Log;

import com.fortvision.minisites.model.Anchor;
import com.fortvision.minisites.model.FVButton;
import com.fortvision.minisites.model.IframeButton;
import com.fortvision.minisites.model.ImageButton;
import com.fortvision.minisites.model.Popup;
import com.fortvision.minisites.model.VideoButton;
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
        //json = new JsonParser().parse("{\"campaign_id\":\"2277\",\"design_id\":7385,\"is_video_campaign\":0,\"preload_popup\":0,\"popup_content\":\"https://fortbuzz.com/new/35\",\"button_width\":80,\"button_height\":120,\"button_initial_side\":\"left\",\"allow_bubble_dismiss\":1,\"dismiss_size\":24,\"button_initial_vertical_position\":\"50%\",\"button_initial_horizontal_position\":\"1%\",\"open_new_tab\":0,\"opacity_timeout\":15,\"opacity_level\":0.2,\"popup_horizontal_margin\":30,\"popup_top_margin\":30,\"popup_bottom_margin\":30,\"twitch_button\":0,\"is_button_iframe\":0,\"button_imgC\":\"https://publicstatic.blob.core.windows.net/staticfiles/FB-Images/FuzeTeaV1.png\",\"button_imgR\":\"https://publicstatic.blob.core.windows.net/staticfiles/FB-Images/FuzeTeaV1.png\",\"button_imgL\":\"https://publicstatic.blob.core.windows.net/staticfiles/FB-Images/FuzeTeaV1.png\",\"button_z_index\":null}");
        Log.d("JSON", json.toString());
        try {
            JsonObject object = json.getAsJsonObject();
            int width = object.get("button_width").getAsInt();
            int height = object.get("button_height").getAsInt();
            boolean dismissible = object.get("allow_bubble_dismiss").getAsInt() == 1;
            int dismissSize = object.get("dismiss_size").getAsInt();
            NumberFormat percentFormat = NumberFormat.getPercentInstance();
            Anchor anchor = new Anchor(percentFormat.parse(object.get("button_initial_horizontal_position").getAsString()).floatValue(),
                    percentFormat.parse(object.get("button_initial_vertical_position").getAsString()).floatValue(),
                    object.get("button_initial_side").getAsString().equalsIgnoreCase("right")
            );
            String campaignId = object.get("campaign_id").getAsString();
            int designId = object.get("design_id").getAsInt();
            float opacity = object.get("opacity_level").getAsFloat();
            int opacityTimeout = object.get("opacity_timeout").getAsInt();

            String popupContent = object.get("popup_content").getAsString(); //"http://castro.co.il";
            boolean preloadPopup = object.get("preload_popup").getAsInt() == 1;
            int popupStartMargin = getJsonElementAsInt(object.get("popup_horizontal_margin"), 0);
            int popupEndMargin = getJsonElementAsInt(object.get("popup_horizontal_margin"), 0);
            int popupTopMargin = getJsonElementAsInt(object.get("popup_top_margin"), 0);
            int popupBottomMargin = getJsonElementAsInt(object.get("popup_bottom_margin"), 0);

            Popup popup = new Popup(popupContent, preloadPopup, popupStartMargin, popupEndMargin, popupTopMargin, popupBottomMargin);
            if (object.get("is_video_campaign").getAsInt() == 1) {
                int bigWidth = object.get("video_width").getAsInt();
                int bigHeight = object.get("video_height").getAsInt();
                return new VideoButton(dismissible, dismissSize, width, height, anchor, campaignId, designId, opacity, opacityTimeout, popup,
                        object.get("video_url").getAsString(), bigWidth, bigHeight);
            } else {
                if (object.get("is_button_iframe").getAsInt() == 1) {
                    return new IframeButton(dismissible, dismissSize, width, height, anchor, campaignId, designId, opacity, opacityTimeout, popup,
                            object.get("button_iframe_url").getAsString());
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
