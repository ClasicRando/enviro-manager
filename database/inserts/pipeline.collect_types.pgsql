insert into pipeline.collect_types (name, description) values
('Download', 'Data is downloaded from the specified URL'),
('FOI', 'Collect data using a FOI request. Contact should be in the data source contact'),
('Scrape', 'Data is scraped from a webpage at the specified URL'),
('Email', 'Data is requested from a data source contact by email'),
('REST', 'Data is collected from an ArcGIS REST service'),
('Other', 'Other unspecified data collection method. See source table or data source notes');
