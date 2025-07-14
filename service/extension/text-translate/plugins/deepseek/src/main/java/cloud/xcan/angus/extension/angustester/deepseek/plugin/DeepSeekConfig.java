package cloud.xcan.angus.extension.angustester.deepseek.plugin;

import cloud.xcan.angus.spec.setting.AppSettingHelper.Setting;
import java.util.Properties;

/**
 * Configuration holder for DeepSeek translation service
 */
public class DeepSeekConfig {

  private String apiKey;
  private String apiEndpoint = "https://api.deepseek.com/chat/completions";
  private String promptTemplate = "Translate the {sourceLanguage} text in the document of the OpenAPI specification below into the corresponding {targetLanguage}. Note: Code blocks, document formats, etc. should not be translated";
  private int maxRetries = 3;
  private long initialRetryDelayMs = 1000;
  private double backoffFactor = 2.0;
  private int timeoutSeconds = 15;

  public DeepSeekConfig() {
  }

  public DeepSeekConfig(String apiKey) {
    this.apiKey = apiKey;
  }

  public DeepSeekConfig(String apiKey, String apiEndpoint, String promptTemplate, int maxRetries,
      long initialRetryDelayMs, double backoffFactor, int timeoutSeconds) {
    this.apiKey = apiKey;
    this.apiEndpoint = apiEndpoint;
    this.promptTemplate = promptTemplate;
    this.maxRetries = maxRetries;
    this.initialRetryDelayMs = initialRetryDelayMs;
    this.backoffFactor = backoffFactor;
    this.timeoutSeconds = timeoutSeconds;
  }

  // Load configuration from Properties
  public DeepSeekConfig fromProperties(Setting setting) {
    this.apiKey = setting.getString("api.key", "");
    this.apiEndpoint = setting.getString("api.endpoint", this.apiEndpoint);
    this.promptTemplate = setting.getString("prompt.template", this.promptTemplate);

    // Retry configuration
    this.maxRetries = setting.getInt("max.retries", this.maxRetries);
    this.initialRetryDelayMs = setting.getLong("retry.initialDelayMs", this.initialRetryDelayMs);
    this.backoffFactor = setting.getDouble("retry.backoffFactor", this.backoffFactor);
    this.timeoutSeconds = setting.getInt("timeout.seconds", this.timeoutSeconds);
    return this;
  }

  // Getters and Setters
  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public String getApiEndpoint() {
    return apiEndpoint;
  }

  public void setApiEndpoint(String apiEndpoint) {
    this.apiEndpoint = apiEndpoint;
  }

  public String getPromptTemplate() {
    return promptTemplate;
  }

  public void setPromptTemplate(String promptTemplate) {
    this.promptTemplate = promptTemplate;
  }

  public int getMaxRetries() {
    return maxRetries;
  }

  public void setMaxRetries(int maxRetries) {
    this.maxRetries = maxRetries;
  }

  public long getInitialRetryDelayMs() {
    return initialRetryDelayMs;
  }

  public void setInitialRetryDelayMs(long initialRetryDelayMs) {
    this.initialRetryDelayMs = initialRetryDelayMs;
  }

  public double getBackoffFactor() {
    return backoffFactor;
  }

  public void setBackoffFactor(double backoffFactor) {
    this.backoffFactor = backoffFactor;
  }

  public int getTimeoutSeconds() {
    return timeoutSeconds;
  }

  public void setTimeoutSeconds(int timeoutSeconds) {
    this.timeoutSeconds = timeoutSeconds;
  }
}
