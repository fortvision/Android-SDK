package com.fortvision.minisites.network;

import android.util.Log;

import com.fortvision.minisites.model.Anchor;
import com.fortvision.minisites.model.FVButton;
import com.fortvision.minisites.model.IframeButton;
import com.fortvision.minisites.model.ImageButton;
import com.fortvision.minisites.model.Popup;
import com.fortvision.minisites.model.VideoButton;
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
        //json = new JsonParser().parse("{\"campaign_id\":\"2277\",\"design_id\":7385,\"is_video_campaign\":0,\"preload_popup\":0,\"popup_content\":\"https://fortbuzz.com/new/35\",\"button_width\":80,\"button_height\":120,\"button_initial_side\":\"left\",\"allow_bubble_dismiss\":1,\"dismiss_size\":24,\"button_initial_vertical_position\":\"50%\",\"button_initial_horizontal_position\":\"1%\",\"open_new_tab\":0,\"opacity_timeout\":15,\"opacity_level\":0.2,\"popup_horizontal_margin\":30,\"popup_top_margin\":30,\"popup_bottom_margin\":30,\"twitch_button\":0,\"is_button_iframe\":0,\"button_imgC\":\"https://publicstatic.blob.core.windows.net/staticfiles/FB-Images/FuzeTeaV1.png\",\"button_imgR\":\"https://publicstatic.blob.core.windows.net/staticfiles/FB-Images/FuzeTeaV1.png\",\"button_imgL\":\"https://publicstatic.blob.core.windows.net/staticfiles/FB-Images/FuzeTeaV1.png\",\"button_z_index\":null}");
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

            String popupContent = element0.get("popup_content").getAsString()+"?useFbWeb=0"; //"http://castro.co.il";
            boolean preloadPopup = element0.get("preload_popup").getAsInt() == 1;

            int popupStartMargin = 0;//getJsonElementAsInt(object.get("popup_horizontal_margin"), 0);
            int popupEndMargin = 0;//getJsonElementAsInt(object.get("popup_horizontal_margin"), 0);
            int popupTopMargin = 0;//getJsonElementAsInt(object.get("popup_top_margin"), 0);
            int popupBottomMargin = 0;//getJsonElementAsInt(object.get("popup_bottom_margin"), 0);

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
