# Bean DataSource Add-On #

Bean DataSource is an Vaadin add-on aimed to provide an easy to use server <-> client data commnunication.

The data sent from server to client is specified using different DataGenerator implementations.

Client-side widget should use com.vaadin.client.data.DataSource interface and have a setter that
can be called through a Connector implementing HasDataSource interface.

Client requests data by callin DataSource method ensureDataAvailable(int start, int length) and this
implementation takes care of requesting the data from the backing Spring repository or a java collection.

Naming conventions are still work in progress. This is not a final product, and all APIs might change. 
Most APIs are not needed by the developers though, so just class names might cause some problems.
