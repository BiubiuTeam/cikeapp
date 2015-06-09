package com.yunkairichu.cike.widget;

/**
 * Created by haowenliang on 15/6/9.
 * @文件描述	: 表情对象实体
 */
public class ChatEmoji {

    /** 表情资源图片对应的ID */
    private int id;

    private int flyEmojiId;

    /** 表情资源对应的文字描述 */
    private String character;

    /** 表情资源的文件名 */
    private String faceName;

    public int getFlyEmojiId(){
        return flyEmojiId;
    }

    public void setFlyEmojiId(int emojiId){
        this.flyEmojiId = emojiId;
    }
    /** 表情资源图片对应的ID */
    public int getId() {
        return id;
    }

    /** 表情资源图片对应的ID */
    public void setId(int id) {
        this.id=id;
    }

    /** 表情资源对应的文字描述 */
    public String getCharacter() {
        return character;
    }

    /** 表情资源对应的文字描述 */
    public void setCharacter(String character) {
        this.character=character;
    }

    /** 表情资源的文件名 */
    public String getFaceName() {
        return faceName;
    }

    /** 表情资源的文件名 */
    public void setFaceName(String faceName) {
        this.faceName=faceName;
    }
}
