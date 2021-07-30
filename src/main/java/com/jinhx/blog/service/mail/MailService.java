package com.jinhx.blog.service.mail;

/**
 * MailService
 *
 * @author jinhx
 * @since 2020-11-07
 */
public interface MailService {

    /**
     * 发送简单邮件的接口
     *
     * @param mail 接收邮箱
     * @param subject 主题
     * @param text 内容
     * @return 发送结果
     */
    boolean sendSimpleMail(String mail, String subject, String text);

    /**
     * 发送带附件邮件的接口
     *
     * @param mail 接收邮箱
     * @param subject 主题
     * @param text 内容
     * @param path 附近路径
     * @return 发送结果
     */
    boolean sendMimeMail(String mail, String subject, String text, String path) throws Exception ;

    /**
     * 发送带附件邮件的接口，并且正文显示附件内容
     *
     * @param mail 接收邮箱
     * @param subject 主题
     * @return 发送结果
     */
    boolean sendMimeMail(String mail, String subject) throws Exception ;

}
