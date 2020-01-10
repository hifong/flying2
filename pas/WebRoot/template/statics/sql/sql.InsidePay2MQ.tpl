{foreach from=$rows item=r key=key}
{literal}{{/literal}"endTime":{$r.END_TIME},"msgType":"FZ_GATEWAY_PAYMENT_RESULT_MSG","payState":"00","payInstruction":"InsidePay","sourceCustomerList":{$r.SOURCE_CUSTOMER_LIST},"targetCustomerList":{$r.TARGET_CUSTOMER_LIST},"transactionNo":"{$r.TRANSACTION_NO}","transactionType":"ZF"{literal}}{/literal}<hr>
{/foreach}